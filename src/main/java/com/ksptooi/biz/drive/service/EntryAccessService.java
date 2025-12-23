package com.ksptooi.biz.drive.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ksptooi.biz.drive.model.EntryPo;
import com.ksptooi.biz.drive.model.vo.EntrySignVo;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptooi.biz.core.service.AttachService;
import com.ksptooi.biz.core.service.AuthService;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import com.ksptooi.biz.drive.repository.EntryRepository;
import com.ksptooi.biz.drive.utils.DriveEntrySignUtils;
import com.ksptooi.commons.config.DriveConfig;
import com.ksptooi.commons.utils.Base64;
import com.google.gson.Gson;

@Service
public class EntryAccessService {

    @Autowired
    private AttachService attachService;        

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private DriveConfig driveConfig;

    private static final Gson gson = new Gson();

    /**
     * 获取条目对象签名 支持单个和多个
     * @param id 条目ID
     * @return 条目对象签名
     * @throws BizException
     * @throws AuthException
     */
    public String getEntrySign(List<Long> ids) throws BizException,AuthException {

        //单文件签名
        if(ids.size() < 2){

            var entryPo = entryRepository.getByIdAndCompanyId(ids.getFirst(), AuthService.requireCompanyId());

            if(entryPo == null){
                throw new BizException("条目不存在!");
            }

            if(entryPo.getKind() == 1){
                throw new BizException("条目类型为文件夹,无法执行签名操作!");
            }

            var attach = entryPo.getAttach();

            if(attach == null){
                throw new BizException("条目中的附件不存在!");
            }

            //生成签名
            var params = new HashMap<String, Object>();
            params.put("cid", entryPo.getCompanyId());
            params.put("eid", entryPo.getId());
            params.put("aid", attach.getId());
            params.put("ek", entryPo.getKind());
            params.put("aPath", attach.getPath());
            params.put("eName", entryPo.getName());
            params.put("t", System.currentTimeMillis());
            params.put("s", driveConfig.getSignSecretKey());
            params.put("isBatch", 0);
            params.put("sign", DriveEntrySignUtils.generateSign(params, driveConfig.getSignSecretKey()));
            return Base64.encodeUrlSafe(gson.toJson(params));
        }

        //多文件签名
        var entryPos = entryRepository.getByIdAndCompanyIds(ids, AuthService.requireCompanyId());

        if(entryPos.isEmpty() || ( entryPos.size() != ids.size() )){
            throw new BizException("至少有一个文件不存在或无权限访问!");
        }

        if(entryPos.size() < 2){
            throw new BizException("至少需要选择2个条目,才能生成签名!");
        }

        //检查是否有文件夹
        for(var entryPo : entryPos){
            if(entryPo.getKind() == 1){
                throw new BizException("多个条目中包含文件夹,无法生成签名!");
            }
        }

        var params = new HashMap<String, Object>();
        params.put("cid", AuthService.requireCompanyId());
        params.put("eids", entryPos.stream().map(EntryPo::getId).map(String::valueOf).collect(Collectors.joining(",")));
        params.put("t", System.currentTimeMillis());
        params.put("s", driveConfig.getSignSecretKey());
        params.put("isBatch", 1);
        params.put("sign", DriveEntrySignUtils.generateSign(params, driveConfig.getSignSecretKey()));
        return Base64.encodeUrlSafe(gson.toJson(params));
    }


    /**
     * 下载条目(单个)
     * 
     * @param signVo 签名信息
     * @return 资源
     * @throws BizException
     * @throws AuthException
     */
    public Resource downloadEntry(EntrySignVo signVo) throws BizException, AuthException {

        if(signVo.getEk() != 0){
            throw new BizException("不支持的文件类型! ");
        }

        //查找文件
        var absolutePath = attachService.getAttachLocalPath(Paths.get(signVo.getAPath()));
        
        if(!Files.exists(absolutePath)){
            throw new BizException("文件在本地存储中不存在! 请重新生成签名并尝试下载.");
        }

        return new FileSystemResource(absolutePath);
    }

    /**
     * 批量下载并打包为ZIP
     */
    public void downloadBatchEntry(EntrySignVo signVo, OutputStream os) throws BizException {
        var idList = Arrays.stream(signVo.getEids().split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        var entries = entryRepository.getByIdAndCompanyIds(idList, signVo.getCid());

        if(entries.isEmpty() || (entries.size() != idList.size())){
            throw new BizException("至少有一个文件不存在或无权限访问!");
        }

        try (var zos = new ZipOutputStream(os)) {

            var nameMap = new HashMap<String, Integer>();

            for (var entry : entries) {

                var attach = entry.getAttach();

                if (attach == null) {
                    throw new BizException("条目中的附件不存在!");
                }

                var path = attachService.getAttachLocalPath(Paths.get(attach.getPath()));
                if (!Files.exists(path)){
                    throw new BizException("文件在本地存储中不存在! 请重新生成签名并尝试下载.");
                }

                // 处理重名文件
                var name = entry.getName();
                if (nameMap.containsKey(name)) {
                    int count = nameMap.get(name) + 1;
                    nameMap.put(name, count);
                    var dotIndex = name.lastIndexOf(".");
                    if (dotIndex > 0) {
                        name = name.substring(0, dotIndex) + "(" + count + ")" + name.substring(dotIndex);
                    } else {
                        name = name + "(" + count + ")";
                    }
                } else {
                    nameMap.put(name, 0);
                }

                var zipEntry = new ZipEntry(name);
                zos.putNextEntry(zipEntry);
                Files.copy(path, zos);
                zos.closeEntry();
            }
        } catch (IOException e) {
            throw new BizException("文件打包下载失败", e);
        }
    }

}

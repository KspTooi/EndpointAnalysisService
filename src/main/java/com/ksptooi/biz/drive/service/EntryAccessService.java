package com.ksptooi.biz.drive.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ksptooi.biz.drive.model.EntryPo;
import com.ksptooi.biz.drive.model.vo.EntrySignVo;
import com.ksptooi.biz.drive.model.vo.MultiEntrySignVo;
import com.ksptooi.biz.core.repository.AttachRepository;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptooi.biz.core.service.AttachService;
import com.ksptooi.biz.core.service.AuthService;
import java.nio.file.Paths;
import java.nio.file.Files;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import com.ksptooi.biz.drive.repository.EntryRepository;
import com.ksptooi.commons.config.DriveConfig;
import com.ksptooi.commons.utils.Base64;
import com.ksptooi.commons.utils.SHA256;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Service
public class EntryAccessService {

    @Autowired
    private AttachService attachService;        

    @Autowired
    private AttachRepository attachRepository;

    @Autowired
    private EntryRepository entryRepository;

    @Autowired
    private DriveConfig driveConfig;

    private static final Gson gson = new Gson();

    /**
     * 获取条目对象签名(单个)
     * @param id 条目ID
     * @return 条目对象签名
     * @throws BizException
     * @throws AuthException
     */
    public String getEntrySign(Long id) throws BizException,AuthException {

        var entryPo = entryRepository.getByIdAndCompanyId(id, AuthService.requireCompanyId());

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

        var cid = entryPo.getCompanyId();
        var eid = entryPo.getId();
        var aid = attach.getId();
        var ek = entryPo.getKind();
        var aPath = attach.getPath();
        var eName = entryPo.getName();
        var t = System.currentTimeMillis();
        var s = driveConfig.getSignSecretKey();

        //生成签名
        var params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("eid", eid);
        params.put("aid", aid);
        params.put("ek", ek);
        params.put("aPath", aPath);
        params.put("eName", eName);
        params.put("t", t);
        params.put("s", s);
        params.put("pSign", SHA256.hex(cid + eid + aid + ek + aPath + eName + t + s));

        var ret = new EntrySignVo();
        ret.setCid(cid);
        ret.setEid(eid);
        ret.setAid(aid);
        ret.setEk(ek);
        ret.setAPath(aPath);
        ret.setEName(eName);
        ret.setT(t);
        ret.setParams(Base64.encodeUrlSafe(gson.toJson(params)));
        return ret.getParams();
    }


    /**
     * 获取条目对象签名(多个)
     * @param ids 条目ID列表
     * @return 条目对象签名
     * @throws BizException
     * @throws AuthException
     */
    public String getEntrySign(List<Long> ids) throws BizException,AuthException {

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

        var cid = AuthService.requireCompanyId();
        var eids = entryPos.stream().map(EntryPo::getId).map(String::valueOf).collect(Collectors.joining(","));
        var t = System.currentTimeMillis();
        var s = driveConfig.getSignSecretKey();

        var params = new HashMap<String, Object>();
        params.put("cid", cid);
        params.put("eids", eids);
        params.put("t", System.currentTimeMillis());
        params.put("s", driveConfig.getSignSecretKey());
        params.put("pSign", SHA256.hex(cid + eids + t + s));

        var ret = new MultiEntrySignVo();
        ret.setCid(cid);
        ret.setEids(eids);
        ret.setT(t);
        ret.setParams(Base64.encodeUrlSafe(gson.toJson(params)));
        return ret.getParams();
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
     * 验证签名
     * 
     * @param base64 签名参数串
     * @return 签名信息
     * @throws BizException
     */
    public EntrySignVo verify(String base64) throws BizException,AuthException {

        try {
            var paramsJson = Base64.decodeUrlSafe(base64);
            var params = gson.fromJson(paramsJson, JsonObject.class);
    
            var cid = params.get("cid").getAsLong();
            var eid = params.get("eid").getAsLong();
            var aid = params.get("aid").getAsLong();
            var ek = params.get("ek").getAsInt();
            var aPath = params.get("aPath").getAsString();
            var eName = params.get("eName").getAsString();
            var t = params.get("t").getAsLong();
            var s = driveConfig.getSignSecretKey();
            var pSign = params.get("pSign").getAsString();
    
            var calcSign = SHA256.hex(cid + eid + aid + ek + aPath + eName + t + s);
    
            if (!pSign.equals(calcSign)) {
                throw new BizException("签名校验失败,提供的签名与计算签名不一致");
            }
    
    
            //判断是否过期
            var ttl = driveConfig.getTtl();
    
            var expireTime = t + ttl * 1000;
    
            if (System.currentTimeMillis() > expireTime) {
                throw new BizException("签名已过期");
            }
    
            var ret = new EntrySignVo();
            ret.setCid(cid);
            ret.setEid(eid);
            ret.setAid(aid);
            ret.setEk(ek);
            ret.setAPath(aPath);
            ret.setEName(eName);
            ret.setT(t);
            ret.setParams(base64);
            return ret;

        } catch (Exception e) {
            throw new BizException("解析签名时发生异常,签名可能不完整或格式错误!", e);
        }
    }

}

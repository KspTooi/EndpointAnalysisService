package com.ksptooi.biz.drive.service;

import com.google.gson.JsonObject;
import com.ksptooi.biz.drive.model.vo.EntrySignVo;
import com.ksptooi.biz.drive.repository.EntryRepository;
import com.ksptooi.commons.config.DriveConfig;
import com.ksptooi.commons.utils.Base64;
import com.ksptooi.commons.utils.SHA256;
import com.google.gson.Gson;
import com.ksptooi.biz.core.service.AuthService;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EntrySignService {

    private static final Gson gson = new Gson();

    @Autowired
    private DriveConfig driveConfig;

    @Autowired
    private EntryRepository entryRepository;
    
    /**
     * 生成签名
     * 
     * @param entryPo 条目
     * @return 签名
     * @throws BizException
     */
    public EntrySignVo sign(Long id) throws BizException,AuthException {

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
        return ret;
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

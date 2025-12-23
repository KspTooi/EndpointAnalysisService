package com.ksptooi.biz.drive.service;

import com.ksptooi.biz.drive.model.EntryPo;
import com.ksptooi.biz.drive.model.vo.DriveSignVo;
import com.ksptooi.biz.drive.repository.EntryRepository;
import com.ksptooi.commons.config.DriveConfig;
import com.ksptooi.commons.utils.SHA256;
import com.ksptooi.biz.core.repository.AttachRepository;
import com.ksptooi.biz.core.service.AuthService;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ksptool.assembly.entity.exception.AuthException;

@Service
public class EntrySignService {

    @Autowired
    private DriveConfig driveConfig;

    @Autowired
    private AttachRepository attachRepository;

    @Autowired
    private EntryRepository entryRepository;
    
    /**
     * 生成签名
     * 
     * @param entryPo 条目
     * @return 签名
     * @throws BizException
     */
    public DriveSignVo sign(Long id) throws BizException,AuthException {

        var entryPo = entryRepository.getByIdAndCompanyId(id, AuthService.requireCompanyId());

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
        var aPath = attach.getPath();
        var t = System.currentTimeMillis();
        var signKey = cid + "_" + eid + "_" + aid + "_" + aPath + "_" + t + "_" + driveConfig.getSignSecretKey();
        var signStr = SHA256.hex(signKey);

        var sign = new StringBuilder();
        sign.append(cid).append("_")
                .append(eid).append("_")
                .append(aid).append("_")
                .append(aPath).append("_")
                .append(t).append("_")
                .append(signStr);
        
        var ret = new DriveSignVo();
        ret.setCid(cid);
        ret.setEid(eid);
        ret.setAid(aid);
        ret.setAPath(aPath);
        ret.setT(t);
        ret.setSign(sign.toString());
        return ret;
    }




    /**
     * 验证签名
     * 
     * @param sign 签名
     * @return 签名信息
     * @throws BizException
     */
    public DriveSignVo verify(String sign) throws BizException,AuthException {

        if (StringUtils.isBlank(sign)) {
            throw new BizException("签名不能为空");
        }

        var parts = sign.split("_");

        if (parts.length != 6) {
            throw new BizException("签名格式错误");
        }

        var cid = parts[0];
        var eid = parts[1];
        var aid = parts[2];
        var aPath = parts[3];
        var t = parts[4];
        var signStr = parts[5];

        var inSignKey = cid + "_" + eid + "_" + aid + "_" + aPath + "_" + t + "_" + driveConfig.getSignSecretKey();
        var inSignStr = SHA256.hex(inSignKey);

        if (!inSignStr.equals(signStr)) {
            throw new BizException("签名验证失败");
        }

        //判断是否过期
        var ttl = driveConfig.getTtl();

        var expireTime = Long.parseLong(t) + ttl * 1000;

        if (System.currentTimeMillis() > expireTime) {
            throw new BizException("签名已过期");
        }

        var ret = new DriveSignVo();
        ret.setCid(Long.parseLong(cid));
        ret.setEid(Long.parseLong(eid));
        ret.setAid(Long.parseLong(aid));
        ret.setAPath(aPath);
        ret.setT(Long.parseLong(t));
        ret.setSign(sign);
        return ret;
    }
}

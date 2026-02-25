package com.ksptool.bio.biz.drive.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.bio.biz.drive.model.driveentry.vo.EntrySignVo;
import com.ksptool.bio.commons.utils.Base64;
import com.ksptool.bio.commons.utils.SHA256;

import java.util.Map;

public class DriveEntrySignUtils {

    private static final Gson gson = new Gson();

    /**
     * 生成签名
     *
     * @param params 参数
     * @return 签名
     * @throws BizException
     */
    public static String generateSign(Map<String, Object> params, String secretKey) throws BizException {

        //判断是单文件还是多文件
        var isBatch = params.get("isBatch");

        if (isBatch == null) {
            throw new BizException("生成签名失败,参数isBatch不能为空!");
        }

        //单签
        if (isBatch.equals(0)) {
            var cid = params.get("cid").toString();
            var eid = params.get("eid").toString();
            var aid = params.get("aid").toString();
            var ek = params.get("ek").toString();
            var aPath = params.get("aPath").toString();
            var eName = params.get("eName").toString();
            var t = params.get("t").toString();
            var s = secretKey;
            return SHA256.hex(cid + eid + aid + ek + aPath + eName + t + isBatch + s);
        }

        //多签
        if (isBatch.equals(1)) {
            var cid = params.get("cid").toString();
            var eids = params.get("eids").toString();
            var t = params.get("t").toString();
            var s = params.get("s").toString();
            return SHA256.hex(cid + eids + t + isBatch + s);
        }

        throw new BizException("生成签名失败,参数isBatch只能是0或1!");
    }

    /**
     * 验证签名
     *
     * @param base64SignWithParams 签名
     * @param secretKey            参数
     * @return 是否验证成功
     * @throws BizException
     */
    public static EntrySignVo parserSignWithParams(String base64SignWithParams, String secretKey) throws BizException {

        try {

            var paramsJson = Base64.decodeUrlSafe(base64SignWithParams);
            var params = gson.fromJson(paramsJson, JsonObject.class);

            if (!params.has("isBatch")) {
                throw new BizException("生成签名失败,参数isBatch不能为空!");
            }

            //是否批量签名 0:否 1:是
            var isBatch = params.get("isBatch").getAsInt();

            //单签
            if (isBatch == 0) {

                if (!params.has("cid") || !params.has("eid") || !params.has("aid") || !params.has("ek") || !params.has("aPath") || !params.has("eName") || !params.has("t")) {
                    throw new BizException("生成签名失败,参数 cid,eid,aid,ek,aPath,eName,t不能为空!");
                }

                var cid = params.get("cid").getAsString();
                var eid = params.get("eid").getAsString();
                var aid = params.get("aid").getAsString();
                var ek = params.get("ek").getAsString();
                var aPath = params.get("aPath").getAsString();
                var eName = params.get("eName").getAsString();
                var t = params.get("t").getAsString();
                var s = secretKey;
                var sign = params.get("sign").getAsString();
                var calcSign = SHA256.hex(cid + eid + aid + ek + aPath + eName + t + isBatch + s);

                if (!sign.equals(calcSign)) {
                    throw new BizException("签名校验失败,提供的签名与计算签名不一致");
                }

                var ret = new EntrySignVo();
                ret.setCid(Long.parseLong(cid));
                ret.setEid(Long.parseLong(eid));
                ret.setAid(Long.parseLong(aid));
                ret.setEk(Integer.parseInt(ek));
                ret.setAPath(aPath);
                ret.setEName(eName);
                ret.setT(Long.parseLong(t));
                ret.setIsBatch(isBatch);
                ret.setParams(base64SignWithParams);
                return ret;
            }

            //多签
            if (!params.has("cid") || !params.has("eids") || !params.has("t")) {
                throw new BizException("生成签名失败,参数 cid,eids,t不能为空!");
            }


            var cid = params.get("cid").getAsString();
            var eids = params.get("eids").getAsString();
            var t = params.get("t").getAsString();
            var s = secretKey;
            var sign = params.get("sign").getAsString();
            var calcSign = SHA256.hex(cid + eids + t + isBatch + s);

            if (!sign.equals(calcSign)) {
                throw new BizException("签名校验失败,提供的签名与计算签名不一致");
            }

            var ret = new EntrySignVo();
            ret.setCid(Long.parseLong(cid));
            ret.setEids(eids);
            ret.setT(Long.parseLong(t));
            ret.setIsBatch(isBatch);
            ret.setParams(base64SignWithParams);
            return ret;
        } catch (Exception e) {
            throw new BizException("解析签名时发生异常,签名可能不完整或格式错误!", e);
        }

    }


}

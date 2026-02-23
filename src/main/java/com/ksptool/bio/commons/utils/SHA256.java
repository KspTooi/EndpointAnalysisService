package com.ksptool.bio.commons.utils;

import com.ksptool.assembly.entity.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
public class SHA256 {

    public static String nextPassword(String password) throws BizException {
        String salt = nextSalt(16);
        String salted = salt + password;
        String hashedPassword = hex(salted);
        return salt + ":" + hashedPassword;
    }

    public static boolean verifyPassword(String password, String hashedPassword) throws BizException {
        String[] parts = hashedPassword.split(":");

        if (parts.length != 2) {
            log.error("原始密码格式错误:{}", hashedPassword);
            throw new BizException("原始密码格式错误 " + hashedPassword);
        }

        String salt = parts[0];
//        log.info("salt:{}", salt);
//        log.info("password:{}",password);
        String hashed = parts[1];
//        log.info("hashed:{}", hashed);
//        log.info("verify:{}", hex(salt + password));
        return hashed.equals(hex(salt + password));
    }

    public static String hex(String input) throws BizException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BizException("加密失败", e);
        }
    }

    // 用于生成盐
    public static String nextSalt(int length) {
        if (length <= 0) {
            return "";
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");

        if (length >= uuid.length()) {
            return uuid;
        }

        return uuid.substring(0, length);
    }
}

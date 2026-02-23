package com.ksptool.bio.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

/**
 * 文件SHA256计算工具
 */
public class FileSha256 {

    /**
     * 计算文件的SHA256值
     *
     * @param filePath 文件路径
     * @return SHA256值（小写十六进制字符串）
     * @throws IOException 读取文件失败
     */
    public static String calculate(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        if (!Files.exists(filePath)) {
            throw new IOException("文件不存在: " + filePath);
        }

        if (!Files.isRegularFile(filePath)) {
            throw new IOException("路径不是文件: " + filePath);
        }

        try (InputStream is = Files.newInputStream(filePath)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }

            byte[] hashBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }

    /**
     * 计算文件的SHA256值（字符串路径）
     *
     * @param filePath 文件路径字符串
     * @return SHA256值（小写十六进制字符串）
     * @throws IOException 读取文件失败
     */
    public static String calculate(String filePath) throws IOException {
        if (StringUtils.isBlank(filePath)) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        return calculate(Paths.get(filePath));
    }

    /**
     * 主函数 - 计算文件SHA256
     *
     * @param args 命令行参数 [文件路径]
     */
    public static void main(String[] args) {

        args = new String[]{"C:\\Users\\kspto\\Desktop\\FileSlice\\com.rar"};

        if (args.length == 0) {
            System.out.println("用法: java FileSha256 <文件路径>");
            System.out.println("示例: java FileSha256 test.txt");
            return;
        }

        String filePath = args[0];
        if (StringUtils.isBlank(filePath)) {
            System.err.println("文件路径不能为空");
            return;
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            System.err.println("文件不存在: " + filePath);
            return;
        }

        try {
            long fileSize = Files.size(path);
            System.out.println("文件路径: " + filePath);
            System.out.println("文件大小: " + fileSize + " 字节");
            System.out.println("----------------------------------------");
            System.out.println("正在计算SHA256...");

            String sha256 = calculate(path);
            System.out.println("SHA256: " + sha256);

        } catch (IOException e) {
            System.err.println("计算SHA256时出错: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("参数错误: " + e.getMessage());
        }
    }
}

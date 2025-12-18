package com.ksptooi.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件分片工具
 */
public class FileSlice {

    /**
     * 分片信息
     */
    public static class SliceInfo {
        private final long index;
        private final long offset;
        private final long size;

        public SliceInfo(long index, long offset, long size) {
            this.index = index;
            this.offset = offset;
            this.size = size;
        }

        public long getIndex() {
            return index;
        }

        public long getOffset() {
            return offset;
        }

        public long getSize() {
            return size;
        }
    }

    /**
     * 计算分片数量
     *
     * @param fileSize 文件大小
     * @param sliceSize 分片大小
     * @return 分片数量
     */
    public static long calculateSliceCount(long fileSize, long sliceSize) {
        if (fileSize <= 0) {
            return 0;
        }
        if (sliceSize <= 0) {
            throw new IllegalArgumentException("分片大小必须大于0");
        }
        return (fileSize + sliceSize - 1) / sliceSize;
    }

    /**
     * 获取文件的所有分片信息
     *
     * @param fileSize 文件大小
     * @param sliceSize 分片大小
     * @return 分片信息列表
     */
    public static List<SliceInfo> getSliceInfos(long fileSize, long sliceSize) {
        if (fileSize <= 0) {
            return new ArrayList<>();
        }
        if (sliceSize <= 0) {
            throw new IllegalArgumentException("分片大小必须大于0");
        }

        long sliceCount = calculateSliceCount(fileSize, sliceSize);
        List<SliceInfo> sliceInfos = new ArrayList<>();

        for (long i = 0; i < sliceCount; i++) {
            long offset = i * sliceSize;
            long size = Math.min(sliceSize, fileSize - offset);
            sliceInfos.add(new SliceInfo(i, offset, size));
        }

        return sliceInfos;
    }

    /**
     * 读取指定分片的数据
     *
     * @param filePath 文件路径
     * @param sliceIndex 分片索引
     * @param sliceSize 分片大小
     * @param fileSize 文件总大小
     * @return 分片数据字节数组
     * @throws IOException 读取失败
     */
    public static byte[] readSlice(Path filePath, long sliceIndex, long sliceSize, long fileSize) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("文件不存在: " + filePath);
        }

        long sliceCount = calculateSliceCount(fileSize, sliceSize);
        if (sliceIndex < 0 || sliceIndex >= sliceCount) {
            throw new IllegalArgumentException("分片索引超出范围: " + sliceIndex + ", 最大为: " + (sliceCount - 1));
        }

        long offset = sliceIndex * sliceSize;
        long actualSize = Math.min(sliceSize, fileSize - offset);

        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r")) {
            raf.seek(offset);
            byte[] buffer = new byte[(int) actualSize];
            int bytesRead = raf.read(buffer);
            if (bytesRead != actualSize) {
                throw new IOException("读取分片数据不完整，期望: " + actualSize + ", 实际: " + bytesRead);
            }
            return buffer;
        }
    }

    /**
     * 读取指定分片的数据流
     *
     * @param filePath 文件路径
     * @param sliceIndex 分片索引
     * @param sliceSize 分片大小
     * @param fileSize 文件总大小
     * @return 分片数据输入流
     * @throws IOException 读取失败
     */
    public static InputStream readSliceAsStream(Path filePath, long sliceIndex, long sliceSize, long fileSize) throws IOException {
        if (!Files.exists(filePath)) {
            throw new IOException("文件不存在: " + filePath);
        }

        long sliceCount = calculateSliceCount(fileSize, sliceSize);
        if (sliceIndex < 0 || sliceIndex >= sliceCount) {
            throw new IllegalArgumentException("分片索引超出范围: " + sliceIndex + ", 最大为: " + (sliceCount - 1));
        }

        long offset = sliceIndex * sliceSize;
        long actualSize = Math.min(sliceSize, fileSize - offset);

        RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r");
        raf.seek(offset);

        return new InputStream() {
            private long remaining = actualSize;

            @Override
            public int read() throws IOException {
                if (remaining <= 0) {
                    return -1;
                }
                int b = raf.read();
                if (b != -1) {
                    remaining--;
                }
                return b;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (remaining <= 0) {
                    return -1;
                }
                int readLen = (int) Math.min(len, remaining);
                int bytesRead = raf.read(b, off, readLen);
                if (bytesRead > 0) {
                    remaining -= bytesRead;
                }
                return bytesRead;
            }

            @Override
            public void close() throws IOException {
                raf.close();
            }
        };
    }

    /**
     * 获取指定分片的信息
     *
     * @param fileSize 文件大小
     * @param sliceSize 分片大小
     * @param sliceIndex 分片索引
     * @return 分片信息
     */
    public static SliceInfo getSliceInfo(long fileSize, long sliceSize, long sliceIndex) {
        if (fileSize <= 0) {
            throw new IllegalArgumentException("文件大小必须大于0");
        }
        if (sliceSize <= 0) {
            throw new IllegalArgumentException("分片大小必须大于0");
        }

        long sliceCount = calculateSliceCount(fileSize, sliceSize);
        if (sliceIndex < 0 || sliceIndex >= sliceCount) {
            throw new IllegalArgumentException("分片索引超出范围: " + sliceIndex + ", 最大为: " + (sliceCount - 1));
        }

        long offset = sliceIndex * sliceSize;
        long size = Math.min(sliceSize, fileSize - offset);
        return new SliceInfo(sliceIndex, offset, size);
    }

    /**
     * 主函数 - 测试文件分片工具
     *
     * @param args 命令行参数 [文件路径] [分片大小(字节)]
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            testBasicFunctions();
            return;
        }

        if (args.length < 2) {
            System.out.println("用法: java FileSlice <文件路径> <分片大小(字节)>");
            System.out.println("示例: java FileSlice test.txt 1024");
            return;
        }

        String filePath = args[0];
        long sliceSize;
        try {
            sliceSize = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("分片大小必须是数字: " + args[1]);
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
            System.out.println("分片大小: " + sliceSize + " 字节");
            System.out.println("----------------------------------------");

            long sliceCount = calculateSliceCount(fileSize, sliceSize);
            System.out.println("分片数量: " + sliceCount);
            System.out.println("----------------------------------------");

            List<SliceInfo> sliceInfos = getSliceInfos(fileSize, sliceSize);
            System.out.println("分片信息列表:");
            for (SliceInfo info : sliceInfos) {
                System.out.printf("  分片[%d]: 偏移量=%d, 大小=%d%n", 
                    info.getIndex(), info.getOffset(), info.getSize());
            }

            if (sliceCount > 0) {
                System.out.println("----------------------------------------");
                System.out.println("读取第一个分片数据:");
                byte[] firstSlice = readSlice(path, 0, sliceSize, fileSize);
                System.out.println("  读取字节数: " + firstSlice.length);
                System.out.println("  前10个字节: " + bytesToHex(firstSlice, Math.min(10, firstSlice.length)));
            }

        } catch (IOException e) {
            System.err.println("处理文件时出错: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("参数错误: " + e.getMessage());
        }
    }

    /**
     * 测试基本功能
     */
    private static void testBasicFunctions() {
        System.out.println("=== 文件分片工具测试 ===");
        System.out.println();

        long fileSize = 10240L;
        long sliceSize = 1024L;

        System.out.println("测试参数:");
        System.out.println("  文件大小: " + fileSize + " 字节");
        System.out.println("  分片大小: " + sliceSize + " 字节");
        System.out.println();

        long sliceCount = calculateSliceCount(fileSize, sliceSize);
        System.out.println("计算结果:");
        System.out.println("  分片数量: " + sliceCount);
        System.out.println();

        List<SliceInfo> sliceInfos = getSliceInfos(fileSize, sliceSize);
        System.out.println("分片信息:");
        for (SliceInfo info : sliceInfos) {
            System.out.printf("  分片[%d]: 偏移量=%d, 大小=%d%n", 
                info.getIndex(), info.getOffset(), info.getSize());
        }
        System.out.println();

        if (sliceCount > 0) {
            SliceInfo firstSlice = getSliceInfo(fileSize, sliceSize, 0);
            System.out.println("第一个分片信息:");
            System.out.printf("  索引: %d%n", firstSlice.getIndex());
            System.out.printf("  偏移量: %d%n", firstSlice.getOffset());
            System.out.printf("  大小: %d%n", firstSlice.getSize());
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    private static String bytesToHex(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        int len = Math.min(length, bytes.length);
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        return sb.toString().trim();
    }
}

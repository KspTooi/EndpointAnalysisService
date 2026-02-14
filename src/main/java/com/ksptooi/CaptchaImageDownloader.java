package com.ksptooi;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CaptchaImageDownloader {

    // --- 配置区域 ---
    // 目标保存路径
    private static final String SAVE_DIR = "captcha_images";
    // 下载数量
    private static final int DOWNLOAD_COUNT = 100;

    // 验证码目标尺寸 (宽 x 高) -> 比如网易易盾常用 320x160
    private static final int TARGET_WIDTH = 600;
    private static final int TARGET_HEIGHT = 360;

    // 图片源 (这里使用 Picsum 随机图库，请求一个比目标稍大的图以保证裁剪质量)
    // 这里的 800/600 代表请求的原图尺寸
    private static final String SOURCE_URL = "https://picsum.photos/800/600";
    // ----------------

    public static void main(String[] args) {
        // 1. 创建保存目录
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("✅ 目录已创建: " + dir.getAbsolutePath());
            }
        }

        System.out.println("🚀 开始下载并处理图片...");

        for (int i = 1; i <= DOWNLOAD_COUNT; i++) {
            try {
                System.out.print("正在处理第 [" + i + "/" + DOWNLOAD_COUNT + "] 张图片... ");

                // 2. 下载图片
                BufferedImage originalImage = downloadImage(SOURCE_URL);
                if (originalImage == null) {
                    System.out.println("❌ 下载失败，跳过。");
                    continue;
                }

                // 3. 执行居中裁剪
                BufferedImage croppedImage = centerCrop(originalImage, TARGET_WIDTH, TARGET_HEIGHT);

                // 4. 保存图片
                String fileName = String.format("bg_%03d.jpg", i);
                File outputFile = new File(dir, fileName);
                ImageIO.write(croppedImage, "jpg", outputFile);

                System.out.println("✅ 已保存: " + fileName);

            } catch (Exception e) {
                System.out.println("❌ 发生异常: " + e.getMessage());
            }
        }
        System.out.println("🎉 所有任务完成！图片保存在: " + dir.getAbsolutePath());
    }

    /**
     * 下载图片并转为 BufferedImage 对象
     */
    private static BufferedImage downloadImage(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 设置 User-Agent 伪装成浏览器，防止部分图库拒绝 Java 请求
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        return ImageIO.read(connection.getInputStream());
    }

    /**
     * 图片居中裁剪算法
     */
    private static BufferedImage centerCrop(BufferedImage original, int targetW, int targetH) {
        int origW = original.getWidth();
        int origH = original.getHeight();

        // 1. 计算裁剪的起始坐标 (x, y)，确保是居中
        // 如果原图比目标小，这里可能会导致负数，实际生产中需要加缩放逻辑，
        // 但因为我们请求的原图足够大，这里直接裁剪即可。
        int x = (origW - targetW) / 2;
        int y = (origH - targetH) / 2;

        // 防止越界
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        int w = Math.min(targetW, origW);
        int h = Math.min(targetH, origH);

        // 2. 利用 Subimage 进行裁剪
        // 注意：getSubimage 共享原图数据，如果不仅是保存还需要修改，建议 new 一个 BufferedImage
        BufferedImage cropped = original.getSubimage(x, y, w, h);

        // 为了防止颜色模式问题（如PNG转JPG变红），新建一个标准 RGB 画布
        BufferedImage result = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();

        // 绘制裁剪后的图到新画布
        g.drawImage(cropped, 0, 0, null);
        g.dispose();

        return result;
    }
}
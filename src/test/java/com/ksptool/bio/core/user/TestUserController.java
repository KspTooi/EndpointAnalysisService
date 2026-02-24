package com.ksptool.bio.core.user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量并发登录测试
 * 账号格式: EAS-T#{number} (0 ~ MAX_USER_INDEX)
 * 密码固定: 123456
 */
@Slf4j
public class TestUserController {

    // ========== 可调参数 ==========
    /**
     * 目标服务地址
     */
    private static final String BASE_URL = "http://127.0.0.1:27500";

    /**
     * 账号编号范围 [0, MAX_USER_INDEX]
     */
    private static final int MAX_USER_INDEX = 1000;

    /**
     * 并发线程数
     */
    private static final int CONCURRENCY = 150;

    /**
     * 每批请求之间的间隔(毫秒)
     */
    private static final long BATCH_INTERVAL_MS = 300;

    /**
     * 固定密码
     */
    private static final String PASSWORD = "123456";
    // ================================

    private static final Gson GSON = new Gson();

    @Test
    public void batchLoginTest() throws InterruptedException {
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        var executor = Executors.newFixedThreadPool(CONCURRENCY);
        var successCount = new AtomicInteger(0);
        var failCount = new AtomicInteger(0);

        List<String> accounts = new ArrayList<>();
        for (int i = 0; i <= MAX_USER_INDEX; i++) {
            accounts.add("EAS-T" + i);
        }

        int total = accounts.size();
        int batchStart = 0;

        while (batchStart < total) {
            int batchEnd = Math.min(batchStart + CONCURRENCY, total);
            List<Future<?>> futures = new ArrayList<>();

            for (int i = batchStart; i < batchEnd; i++) {
                String username = accounts.get(i);
                futures.add(executor.submit(() -> {
                    try {
                        String token = login(client, username, PASSWORD);
                        if (token != null) {
                            log.info("[SUCCESS] username={} token={}", username, token);
                            successCount.incrementAndGet();
                        } else {
                            log.warn("[FAILED]  username={} 登录失败或无token返回", username);
                            failCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        log.error("[ERROR]   username={} 异常: {}", username, e.getMessage());
                        failCount.incrementAndGet();
                    }
                }));
            }

            for (Future<?> f : futures) {
                try {
                    f.get();
                } catch (ExecutionException ignored) {
                }
            }

            batchStart = batchEnd;

            if (batchStart < total && BATCH_INTERVAL_MS > 0) {
                Thread.sleep(BATCH_INTERVAL_MS);
            }
        }

        executor.shutdown();
        log.info("========== 测试完成 总账号={} 成功={} 失败={} ==========",
                total, successCount.get(), failCount.get());
    }

    /**
     * 发送登录请求，返回 sessionId (token)，失败返回 null
     */
    private String login(HttpClient client, String username, String password) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/userLogin"))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        var json = GSON.fromJson(responseBody, JsonObject.class);

        if (json == null || !json.has("data") || json.get("data").isJsonNull()) {
            return null;
        }

        var data = json.getAsJsonObject("data");
        if (!data.has("sessionId") || data.get("sessionId").isJsonNull()) {
            return null;
        }

        return data.get("sessionId").getAsString();
    }

}

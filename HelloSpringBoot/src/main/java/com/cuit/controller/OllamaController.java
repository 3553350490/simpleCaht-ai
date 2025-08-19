package com.cuit.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*")
public class OllamaController {

    private final WebClient webClient;
    private final StringRedisTemplate redisTemplate;

    @Value("${ollama.model}")
    private String modelName;

    public OllamaController(@Value("${ollama.base-url}") String baseUrl, StringRedisTemplate redisTemplate) {
        this.webClient = WebClient.create(baseUrl);
        this.redisTemplate = redisTemplate;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String prompt, @RequestParam String sessionId) {
        String redisKey = "chat:history:" + sessionId;

        // 取 Redis 历史
        List<String> history = redisTemplate.opsForList().range(redisKey, 0, -1);

        // 拼接上下文
        StringBuilder context = new StringBuilder();
        if (history != null) {
            for (String msg : history) {
                context.append(msg).append("\n");
            }
        }
        context.append("用户: ").append(prompt).append("\nAI: ");

        // 保存用户输入到 Redis
        redisTemplate.opsForList().rightPush(redisKey, "用户: " + prompt);
        redisTemplate.opsForList().trim(redisKey, -20, -1);

        StringBuilder aiReplyBuilder = new StringBuilder();

        return webClient.post()
                .uri("/api/generate")
                .bodyValue(Map.of(
                        "model", modelName,
                        "prompt", context.toString(),
                        "stream", true
                ))
                .retrieve()
                .bodyToFlux(Map.class)
                .map(json -> {
                    String resp = (String) json.getOrDefault("response", "");

                    // 1. 过滤 <think> 标签
                    resp = resp.replaceAll("(?s)<think>.*?</think>", "").trim();

                    // 2. 简单 Markdown 格式化，例如换行处理
                    resp = resp.replaceAll("\n", "\n\n");

                    if (!resp.isEmpty()) {
                        aiReplyBuilder.append(resp);
                    }
                    return resp;
                })
                .filter(s -> !s.isEmpty())
                .doOnComplete(() -> {
                    if (aiReplyBuilder.length() > 0) {
                        redisTemplate.opsForList().rightPush(redisKey, "AI: " + aiReplyBuilder.toString());
                        redisTemplate.opsForList().trim(redisKey, -20, -1);
                    }
                });
    }
}

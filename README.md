* 写在前面的话：
* 这个项目是基于springboot+vue+element-ui+ollama+redis的项目，若要使用，建议使用intelliJ IDEA开发
* 项目比较简单，并且项目内附有springboot入门实验程序，适合刚接触到springboot的人尝试，特别是想入门AI-api调用的java学习者。
* 项目内部注释也比较详细，希望在您学习和扩展时提供帮助*——*

# 🖥 前端（Vue 组件 `Chat.vue`）

### 功能

* 显示聊天界面（气泡 UI）
* 发送用户输入的消息
* 通过 **Server-Sent Events (SSE)** 实现 **AI 流式回复**
* 使用 `marked` 渲染 Markdown

### 核心点

1. **消息发送**

   ```js
   sendPrompt() {
     if (!this.prompt) return;

     // 添加用户消息
     this.messages.push({ role: "user", content: this.prompt });
     const aiMsg = { role: "ai", content: "" };
     this.messages.push(aiMsg);

     const url = `http://localhost:8090/ai/stream?prompt=${encodeURIComponent(this.prompt)}&sessionId=${this.sessionId}`;
     const eventSource = new EventSource(url);
   }
   ```

   * 用户输入消息后，调用后端接口 `/ai/stream`
   * `EventSource` 用于接收 **流式响应**（SSE）

2. **流式接收 AI 回复**

   ```js
   eventSource.onmessage = (event) => {
     buffer += event.data;
     const filtered = buffer.replace(/<think>[\s\S]*?<\/think>/g, "").trim();
     aiMsg.content = filtered;
   };
   ```

   * `eventSource.onmessage` 每次接收一块数据
   * `buffer` 用来累积
   * 过滤掉 `<think>...</think>` 标签（避免模型内部思考内容暴露）
   * 更新消息内容并自动滚动到底部

3. **Markdown 渲染**

   ```js
   renderMessage(content) {
     const safeContent = content.replace(/</g, "&lt;").replace(/>/g, "&gt;");
     return marked.parse(safeContent);
   }
   ```

   * 防止 **HTML 注入攻击**
   * 用 `marked` 将文本转换为 HTML（支持代码块、列表等）

---

# ⚙️ 后端（`OllamaController`）

### 功能

* 提供 `/ai/stream` 接口（返回 **SSE**）
* 拼接上下文（带历史记录）
* 调用 **Ollama 模型** API (`/api/generate`)
* 使用 **Redis 保存历史会话**

### 核心点

1. **接收参数**

   ```java
   @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
   public Flux<String> stream(@RequestParam String prompt, @RequestParam String sessionId)
   ```

   * `prompt` → 用户输入
   * `sessionId` → 用来区分不同会话

2. **读取历史上下文**

   ```java
   String redisKey = "chat:history:" + sessionId;
   List<String> history = redisTemplate.opsForList().range(redisKey, 0, -1);
   StringBuilder context = new StringBuilder();
   if (history != null) {
       for (String msg : history) {
           context.append(msg).append("\n");
       }
   }
   context.append("用户: ").append(prompt).append("\nAI: ");
   ```

   * 从 Redis 取出历史对话
   * 拼接成完整上下文，保证 AI 具备连续对话能力

3. **调用 Ollama**

   ```java
   return webClient.post()
           .uri("/api/generate")
           .bodyValue(Map.of(
                   "model", modelName,
                   "prompt", context.toString(),
                   "stream", true
           ))
           .retrieve()
           .bodyToFlux(Map.class)
   ```

   * 通过 WebClient 调用 Ollama 的 `/api/generate`
   * 参数中 `stream: true` 表示要求流式输出

4. **处理流式数据**

   ```java
   .map(json -> {
       String resp = (String) json.getOrDefault("response", "");
       resp = resp.replaceAll("(?s)<think>.*?</think>", "").trim(); // 过滤 <think>
       resp = resp.replaceAll("\n", "\n\n"); // 美化换行
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
   ```

   * 每次流式响应数据到达，就更新消息
   * 过滤 `<think>` 标签
   * 对换行做 Markdown 兼容处理
   * 在流结束后把 AI 最终回复存到 Redis，并限制历史最多 20 条

---

# 🔑 整体流程

1. 用户在前端输入消息 → 调用 `/ai/stream`
2. 后端拼接历史对话 + 新输入 → 请求 Ollama
3. Ollama 流式输出 → 后端 SSE 推送给前端
4. 前端逐字渲染，实时显示 AI 回复
5. 聊天历史存入 Redis，实现上下文记忆

---

✅ 总结：

* **前端**：Vue + Element UI + SSE，实现实时对话 UI + Markdown 渲染
* **后端**：Spring Boot (WebFlux) + Redis + WebClient 调用 Ollama，提供流式 AI 接口
* **存储**：Redis 保存会话上下文，保证连续对话体验

---


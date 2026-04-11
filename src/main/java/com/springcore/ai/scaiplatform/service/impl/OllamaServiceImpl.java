package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.service.api.OllamaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class OllamaServiceImpl implements OllamaService {

    private final ChatModel chatModel;
    public OllamaServiceImpl(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String model, String prompt) {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (!StringUtils.hasLength(model)) {
            model = chatModel.getDefaultOptions().getModel();
            String response = chatModel.call(prompt);

            stopWatch.stop();
            log.info("Model '{}' call completed in {} ms.",
                    model, stopWatch.getTotalTimeMillis());
            return response;
        }

        // Dynamic Model
        OllamaOptions ollamaOptions = new OllamaOptions();
        ollamaOptions.setModel(model);
        Prompt newPrompt = new Prompt(new UserMessage(prompt), ollamaOptions);
        log.info("Use Model: {}", model);
        ChatResponse call = chatModel.call(newPrompt);
        stopWatch.stop();

        Generation result = call.getResult();
        AssistantMessage output = result.getOutput();
        String response =output.getText();
        log.info("Model '{}' call completed in {} ms.",
                model, stopWatch.getTotalTimeMillis());
        return response;
    }

    // *** เปลี่ยนจาก String เป็น Flux<String> เพื่อรองรับ Stream ***
    public Flux<String> chatStream(String model, String prompt) {
        log.info("Received chat prompt: {}", prompt);
        Prompt newPrompt;
        if (StringUtils.isEmpty(model)) {
            newPrompt = new Prompt(new UserMessage(prompt));
        } else {
            OllamaOptions ollamaOptions = new OllamaOptions();
            ollamaOptions.setModel(model);
            newPrompt = new Prompt(new UserMessage(prompt), ollamaOptions);
            log.info("Use Model (Streaming): {}", model);
        }

        // 2. เรียกใช้เมธอด stream()
        Flux<ChatResponse> responseFlux = chatModel.stream(newPrompt);

        // 3. แปลง Flux<ChatResponse> ให้เป็น Flux<String> (เฉพาะข้อความ)
        return responseFlux
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .mapNotNull(AbstractMessage::getText)
                .filter(StringUtils::hasLength)
                .doFinally(signalType -> log.info("Model '{}' streaming completed.", model));
    }
}

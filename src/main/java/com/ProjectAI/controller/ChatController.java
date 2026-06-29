package com.ProjectAI.controller;

import com.ProjectAI.dto.chat.ChatRequest;
import com.ProjectAI.dto.chat.ChatResponse;
import com.ProjectAI.service.AiGenerationService;
import com.ProjectAI.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final AiGenerationService aiGenerationService;
    @PostMapping(value = "/api/chat/stream" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest chatRequest) {

        return aiGenerationService.streamResponse(chatRequest.message() , chatRequest.projectId())
                .map(data -> ServerSentEvent.<String>builder()
                        .data(data)
                        .build());
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ChatResponse>> getChatHistory(@PathVariable Long projectId) {
        return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));

    }
}

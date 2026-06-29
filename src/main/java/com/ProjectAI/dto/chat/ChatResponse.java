package com.ProjectAI.dto.chat;

import com.ProjectAI.entity.ChatEvent;
import com.ProjectAI.entity.ChatSession;
import com.ProjectAI.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        ChatSession chatSession,
        MessageRole role,
        List<ChatEvent> events,
        String content,
        Integer tokenUsed,
        Instant createdAt

) {
}

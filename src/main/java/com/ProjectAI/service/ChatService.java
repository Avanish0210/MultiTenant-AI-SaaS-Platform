package com.ProjectAI.service;

import com.ProjectAI.dto.chat.ChatResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ChatService {
    List<ChatResponse> getProjectChatHistory(Long projectId);
}

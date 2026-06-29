package com.ProjectAI.service.impl;

import com.ProjectAI.dto.chat.ChatResponse;
import com.ProjectAI.entity.ChatMessage;
import com.ProjectAI.entity.ChatSession;
import com.ProjectAI.entity.ChatSessionId;
import com.ProjectAI.mapper.ChatMapper;
import com.ProjectAI.repository.ChatMessageRepository;
import com.ProjectAI.repository.ChatSessionRepository;
import com.ProjectAI.security.AuthUtil;
import com.ProjectAI.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId, userId)
        );

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);

        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}

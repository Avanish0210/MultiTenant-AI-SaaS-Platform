package com.ProjectAI.mapper;

import com.ProjectAI.dto.chat.ChatResponse;
import com.ProjectAI.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);
}

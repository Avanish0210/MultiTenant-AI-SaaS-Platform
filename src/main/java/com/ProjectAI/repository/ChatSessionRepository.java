package com.ProjectAI.repository;

import com.ProjectAI.entity.ChatSession;
import com.ProjectAI.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}

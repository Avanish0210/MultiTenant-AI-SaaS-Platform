package com.ProjectAI.entity;

import com.ProjectAI.enums.ChatEventType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chat_events")
public class ChatEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ChatMessage chatMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatEventType type;

    @Column(nullable = false)
    private Integer sequenceOrder;

    @Column(columnDefinition = "text")
    private String content;

    private String filePath;

    @Column(nullable = false)
    private String metadata;
}

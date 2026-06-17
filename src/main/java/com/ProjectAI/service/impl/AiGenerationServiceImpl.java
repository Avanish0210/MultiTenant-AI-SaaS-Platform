package com.ProjectAI.service.impl;

import com.ProjectAI.service.AiGenerationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiGenerationServiceImpl implements AiGenerationService {

    @Override
    public Flux<String> streamResponse(String message, Long aLong) {
        return null;
    }
}

package com.phamthanhlong.identity_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.phamthanhlong.identity_service.repository.InvalidatedTokenRepository;

@Service
public class CleanUpToken {
    @Autowired
    private InvalidatedTokenRepository repository;

    @Scheduled(cron = "0 0 0 * * ?") // chạy mỗi ngày lúc 00:00
    public void cleanupExpiredTokens() {
        repository.deleteAllExpiredTokens(LocalDateTime.now());
    }
}

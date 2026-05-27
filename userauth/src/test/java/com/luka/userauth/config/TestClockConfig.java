package com.luka.userauth.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
@Profile("test")
public class TestClockConfig {

    @Bean
    public Clock clock(){
        return Clock.fixed(
                Instant.parse("2026-05-09T12:00:00Z"),
                ZoneId.of("Europe/Belgrade"));
    }

}

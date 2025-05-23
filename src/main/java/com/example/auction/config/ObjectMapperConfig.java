package com.example.auction.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@Configuration
public class ObjectMapperConfig {
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		// Java 8 날짜/시간 지원 모듈
		JavaTimeModule javaTimeModule = new JavaTimeModule();

		// LocalDateTime ISO-8601 형식 (예: "2025-05-23T15:30:00")
		javaTimeModule.addDeserializer(
			LocalDateTime.class,
			new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
		);

		objectMapper.registerModule(javaTimeModule);

		// 날짜를 timestamp가 아닌 ISO 문자열로 출력
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		return objectMapper;
	}
}

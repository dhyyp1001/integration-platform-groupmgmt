package com.keti.aiot.integration_platform_groupmgmt.domain.devicegroup.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class DeviceSearchCondition {

    @Schema(description = "단말 ID (LIKE 검색)", example = "TEMP01", nullable = true)
    private String devId;

    @Schema(description = "단말 EUI (LIKE 검색)", example = "EUI123456789", nullable = true)
    private String devEui;

    @Schema(description = "NC ID (LIKE 검색)", example = "NC001", nullable = true)
    private String ncId;

    @Schema(description = "단말 설명 (LIKE 검색)", example = "온도센서", nullable = true)
    private String description;

    @Schema(description = "생성일 시작 (예: 2024-01-01T00:00:00)", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAtFrom;

    @Schema(description = "생성일 종료", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAtTo;

    @Schema(description = "수정일 시작", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modifiedAtFrom;

    @Schema(description = "수정일 종료", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modifiedAtTo;

    @Schema(description = "마지막 통신일 시작", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastSeenFrom;

    @Schema(description = "마지막 통신일 종료", nullable = true)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastSeenTo;
}


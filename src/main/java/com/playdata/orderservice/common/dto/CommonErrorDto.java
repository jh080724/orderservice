package com.playdata.orderservice.common.dto;

import org.springframework.http.HttpStatus;

public class CommonErrorDto {
    private int statusCode;
    private String statusMessage;

    public CommonErrorDto(HttpStatus httpStatus, String statusMessage) {
        this.statusCode = httpStatus.value();
        this.statusMessage = statusMessage;
    }
}

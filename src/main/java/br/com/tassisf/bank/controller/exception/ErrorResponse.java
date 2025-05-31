package br.com.tassisf.bank.controller.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(Integer status, String message, String path,
                            LocalDateTime timestamp) {
}

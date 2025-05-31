package br.com.tassisf.bank.controller.exception;

import java.time.LocalDateTime;

public record ErrorResponse(Integer status, String message, String path,
                            LocalDateTime timestamp) {
}

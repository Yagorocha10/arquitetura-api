package br.com.cinbesa.arquitetura_exemplo.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String mensagem
) {
}

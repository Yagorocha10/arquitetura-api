package br.com.cinbesa.arquitetura_exemplo.dto;

import java.time.LocalDateTime;

public record FolderResponseDTO(
        Long id,
        String nome,
        LocalDateTime dataCriacao
) {
}

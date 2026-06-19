package br.com.cinbesa.arquitetura_exemplo.dto;

import java.time.LocalDateTime;

public record DocumentResponseDTO(
        Long id,
        String nome,
        String tipo,
        Long folderId,
        String mimeType,
        Long tamanho,
        LocalDateTime dataCriacao
) {
}

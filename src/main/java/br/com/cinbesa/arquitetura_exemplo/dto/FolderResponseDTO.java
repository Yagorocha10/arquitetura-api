package br.com.cinbesa.arquitetura_exemplo.dto;

import java.time.LocalDateTime;
import java.util.List;

public record FolderResponseDTO(
        Long id,
        String nome,
        LocalDateTime dataCriacao,
        Long parentId,
        List<FolderResponseDTO> children
) {
}

package br.com.cinbesa.arquitetura_exemplo.dto;

public record StorageResponseDTO(
        Long totalBytes,
        String espacoUtilizado,
        Long quantidadeArquivos
) {
}

package br.com.cinbesa.arquitetura_exemplo.dto;

public record DocumentContentDTO(
        String nome,
        String mimeType,
        byte[] conteudo
) {
}

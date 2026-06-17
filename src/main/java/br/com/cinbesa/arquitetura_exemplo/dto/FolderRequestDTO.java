package br.com.cinbesa.arquitetura_exemplo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FolderRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(
                min = 2,
                max = 100,
                message = "Nome deve ter entre 2 e 100 caracteres"
        )
        String nome
) {
}

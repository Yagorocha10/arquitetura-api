package br.com.cinbesa.arquitetura_exemplo.exception;

public class FolderNotFoundException extends RuntimeException{

    public FolderNotFoundException(Long id) {
        super("Pasta com ID " + id + " não encontrada.");
    }
}

package br.com.cinbesa.arquitetura_exemplo.exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(Long id) {
        super("Documento nao encontrado: " + id);
    }
}

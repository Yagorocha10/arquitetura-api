package br.com.cinbesa.arquitetura_exemplo.controller;

import br.com.cinbesa.arquitetura_exemplo.dto.DocumentContentDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.DocumentResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.StorageResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/documents")
    public List<DocumentResponseDTO> listarTodos() {
        return documentService.listarTodos();
    }

    @GetMapping("/documents/storage")
    public StorageResponseDTO storageInfo() {
        return documentService.storageInfo();
    }

    @GetMapping("/folders/{folderId}/documents")
    public List<DocumentResponseDTO> listarPorPasta(@PathVariable Long folderId) {
        return documentService.listarPorPasta(folderId);
    }

    @PostMapping(value = "/folders/{folderId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentResponseDTO upload(@PathVariable Long folderId, @RequestPart("file") MultipartFile file) throws IOException {
        return documentService.upload(folderId, file);
    }

    @GetMapping("/documents/{id}/view")
    public ResponseEntity<byte[]> visualizar(@PathVariable Long id) {
        DocumentContentDTO document = documentService.conteudo(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(document.nome(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(document.conteudo());
    }

    @GetMapping("/documents/{id}/download")
    public ResponseEntity<byte[]> baixar(@PathVariable Long id) {
        DocumentContentDTO document = documentService.conteudo(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(document.nome(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(document.conteudo());
    }

    @DeleteMapping("/documents/{id}")
    public void excluir(@PathVariable Long id) {
        documentService.excluir(id);
    }
}

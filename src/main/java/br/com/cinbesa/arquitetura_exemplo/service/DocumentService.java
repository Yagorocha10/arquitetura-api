package br.com.cinbesa.arquitetura_exemplo.service;

import br.com.cinbesa.arquitetura_exemplo.dto.DocumentContentDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.DocumentResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.StorageResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.entity.Folder;
import br.com.cinbesa.arquitetura_exemplo.entity.StoredDocument;
import br.com.cinbesa.arquitetura_exemplo.exception.DocumentNotFoundException;
import br.com.cinbesa.arquitetura_exemplo.exception.FolderNotFoundException;
import br.com.cinbesa.arquitetura_exemplo.repository.DocumentRepository;
import br.com.cinbesa.arquitetura_exemplo.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FolderRepository folderRepository;

    @Transactional
    public DocumentResponseDTO upload(Long folderId, MultipartFile file) throws IOException {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));

        StoredDocument document = StoredDocument.builder()
                .nome(file.getOriginalFilename() == null ? "arquivo" : file.getOriginalFilename())
                .tipo(resolveType(file.getOriginalFilename()))
                .mimeType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                .tamanho(file.getSize())
                .dataCriacao(LocalDateTime.now())
                .conteudo(file.getBytes())
                .folder(folder)
                .build();

        return toResponse(documentRepository.save(document));
    }

    public List<DocumentResponseDTO> listarTodos() {
        return documentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DocumentResponseDTO> listarPorPasta(Long folderId) {
        if (!folderRepository.existsById(folderId)) {
            throw new FolderNotFoundException(folderId);
        }

        return documentRepository.findByFolderId(folderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DocumentContentDTO conteudo(Long id) {
        StoredDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        return new DocumentContentDTO(
                document.getNome(),
                document.getMimeType(),
                document.getConteudo()
        );
    }

    public void excluir(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new DocumentNotFoundException(id);
        }

        documentRepository.deleteById(id);
    }

    private DocumentResponseDTO toResponse(StoredDocument document) {
        return new DocumentResponseDTO(
                document.getId(),
                document.getNome(),
                document.getTipo(),
                document.getFolder().getId(),
                document.getMimeType(),
                document.getTamanho(),
                document.getDataCriacao()
        );
    }

    private String formatarEspaco(Long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }

        double kb = bytes / 1024.0;

        if (kb < 1024) {
            return String.format("%.2f KB", kb);
        }

        double mb = kb / 1024.0;

        if (mb < 1024) {
            return String.format("%.2f MB", mb);
        }

        double gb = mb / 1024.0;

        return String.format("%.2f GB", gb);

    }

    private String resolveType(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "UNKNOWN";
        }

        return filename.substring(filename.lastIndexOf('.') + 1).toUpperCase();
    }

    public StorageResponseDTO consultarArmazenamento() {
        Long totalBytes = documentRepository.calcularEspacoUtilizado();

        Long quantidadeArquivos = documentRepository.quantidadeArquivos();


        return new StorageResponseDTO(
                totalBytes,
                formatarEspaco(totalBytes),
                quantidadeArquivos
        );
    }
}

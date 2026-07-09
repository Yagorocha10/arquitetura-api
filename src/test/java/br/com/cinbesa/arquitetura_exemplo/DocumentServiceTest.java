package br.com.cinbesa.arquitetura_exemplo;


import br.com.cinbesa.arquitetura_exemplo.dto.DocumentContentDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.DocumentResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.StorageResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.entity.Folder;
import br.com.cinbesa.arquitetura_exemplo.entity.StoredDocument;
import br.com.cinbesa.arquitetura_exemplo.exception.DocumentNotFoundException;
import br.com.cinbesa.arquitetura_exemplo.exception.FolderNotFoundException;
import br.com.cinbesa.arquitetura_exemplo.repository.DocumentRepository;
import br.com.cinbesa.arquitetura_exemplo.repository.FolderRepository;
import br.com.cinbesa.arquitetura_exemplo.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private DocumentService documentService;



    @Test
    void deveFazerUploadDocumento() throws Exception {

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .build();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "curriculo.pdf",
                        "application/pdf",
                        "Olá, mundo".getBytes()
                );

        StoredDocument document = StoredDocument.builder()
                .id(1L)
                .nome("curriculo.pdf")
                .tipo("PDF")
                .mimeType("application/pdf")
                .tamanho(file.getSize())
                .conteudo(file.getBytes())
                .folder(folder)
                .dataCriacao(LocalDateTime.now())
                .build();

        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(folder));

        when(documentRepository.save(any(StoredDocument.class)))
                .thenReturn(document);

        DocumentResponseDTO response =
                documentService.upload(1L, file);

        assertNotNull(response);

        assertEquals(1L, response.id());

        assertEquals("curriculo.pdf", response.nome());

        assertEquals("PDF", response.tipo());

        assertEquals(1L, response.folderId());

        verify(folderRepository)
                .findById(1L);

        verify(documentRepository)
                .save(any(StoredDocument.class));

    }

    @Test
    void deveLancarExcecaoAoFazerUploadEmPastaInexistente() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "curriculo.pdf",
                        "application/pdf",
                        "conteudo".getBytes()
                );

        when(folderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                FolderNotFoundException.class,
                () -> documentService.upload(99L, file)
        );

        verify(folderRepository).findById(99L);

        verify(documentRepository, never()).save(any(StoredDocument.class));
    }

    @Test
    void deveResolverTipoComoUnknownQuandoArquivoSemExtensao() throws Exception {

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .build();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "semextensao",
                        "application/octet-stream",
                        "conteudo".getBytes()
                );

        StoredDocument document = StoredDocument.builder()
                .id(1L)
                .nome("semextensao")
                .tipo("UNKNOWN")
                .mimeType("application/octet-stream")
                .tamanho(file.getSize())
                .conteudo(file.getBytes())
                .folder(folder)
                .dataCriacao(LocalDateTime.now())
                .build();

        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(folder));

        when(documentRepository.save(any(StoredDocument.class)))
                .thenReturn(document);

        DocumentResponseDTO response =
                documentService.upload(1L, file);

        assertNotNull(response);

        assertEquals("UNKNOWN", response.tipo());
    }

    @Test
    void deveFazerUploadComNomeNuloUsandoNomePadrao() throws Exception {

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .build();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        null,
                        "application/octet-stream",
                        "conteudo".getBytes()
                );

        StoredDocument document = StoredDocument.builder()
                .id(1L)
                .nome("arquivo")
                .tipo("UNKNOWN")
                .mimeType("application/octet-stream")
                .tamanho(file.getSize())
                .conteudo(file.getBytes())
                .folder(folder)
                .dataCriacao(LocalDateTime.now())
                .build();

        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(folder));

        when(documentRepository.save(any(StoredDocument.class)))
                .thenReturn(document);

        DocumentResponseDTO response =
                documentService.upload(1L, file);

        assertNotNull(response);

        assertEquals("arquivo", response.nome());

        assertEquals("UNKNOWN", response.tipo());
    }

    @Test
    void deveListarTodosDocumentos() {

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .build();

        StoredDocument document1 = StoredDocument.builder()
                .id(1L)
                .nome("curriculo.pdf")
                .tipo("PDF")
                .mimeType("application/pdf")
                .tamanho(1024L)
                .folder(folder)
                .dataCriacao(LocalDateTime.now())
                .build();

        StoredDocument document2 = StoredDocument.builder()
                .id(2L)
                .nome("foto.png")
                .tipo("PNG")
                .mimeType("image/png")
                .tamanho(2048L)
                .folder(folder)
                .dataCriacao(LocalDateTime.now())
                .build();

        when(documentRepository.findAll())
                .thenReturn(List.of(document1, document2));

        List<DocumentResponseDTO> response =
                documentService.listarTodos();

        assertNotNull(response);

        assertEquals(2, response.size());

        assertEquals("curriculo.pdf", response.get(0).nome());

        assertEquals("foto.png", response.get(1).nome());

        verify(documentRepository).findAll();

    }

    @Test
    void deveListarTodosDocumentosQuandoNaoHaDocumentos() {

        when(documentRepository.findAll())
                .thenReturn(List.of());

        List<DocumentResponseDTO> response =
                documentService.listarTodos();

        assertNotNull(response);

        assertTrue(response.isEmpty());

        verify(documentRepository).findAll();
    }

    @Test
    void deveListarDocumentosPorPasta() {

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .build();

        StoredDocument document1 = StoredDocument.builder()
                .id(1L)
                .nome("curriculo.pdf")
                .tipo("PDF")
                .mimeType("application/pdf")
                .tamanho(1024L)
                .folder(folder)
                .dataCriacao(LocalDateTime.now())
                .build();

        StoredDocument document2 = StoredDocument.builder()
                .id(2L)
                .nome("foto.png")
                .tipo("PNG")
                .mimeType("image/png")
                .tamanho(2048L)
                .folder(folder)
                .dataCriacao(LocalDateTime.now())
                .build();

        when(folderRepository.existsById(1L))
                .thenReturn(true);

        when(documentRepository.findByFolderId(1L))
                .thenReturn(List.of(document1, document2));

        List<DocumentResponseDTO> response =
                documentService.listarPorPasta(1L);

        assertNotNull(response);

        assertEquals(2, response.size());

        assertEquals("curriculo.pdf", response.get(0).nome());

        assertEquals("foto.png", response.get(1).nome());

        verify(folderRepository).existsById(1L);

        verify(documentRepository).findByFolderId(1L);

    }

    @Test
    void deveListarPastaVaziaDeDocumentos() {

        when(folderRepository.existsById(1L))
                .thenReturn(true);

        when(documentRepository.findByFolderId(1L))
                .thenReturn(List.of());

        List<DocumentResponseDTO> response =
                documentService.listarPorPasta(1L);

        assertNotNull(response);

        assertTrue(response.isEmpty());

        verify(folderRepository).existsById(1L);

        verify(documentRepository).findByFolderId(1L);
    }

    @Test
    void deveRetornarConteudoDocumento() {

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .build();

        byte[] conteudo = "Olá, mundo".getBytes();

        StoredDocument document = StoredDocument.builder()
                .id(1L)
                .nome("curriculo.pdf")
                .tipo("PDF")
                .mimeType("application/pdf")
                .tamanho((long) conteudo.length)
                .conteudo(conteudo)
                .folder(folder)
                .dataCriacao(LocalDateTime.now())
                .build();


        when(documentRepository.findById(1L))
                .thenReturn(Optional.of(document));

        DocumentContentDTO response =
                documentService.conteudo(1L);

        assertNotNull(response);

        assertEquals("curriculo.pdf", response.nome());

        assertEquals("application/pdf", response.mimeType());

        assertArrayEquals(conteudo, response.conteudo());

        verify(documentRepository).findById(1L);



    }

    @Test
    void deveExcluirDocumento() {

        when(documentRepository.existsById(1L))
                .thenReturn(true);


        documentService.excluir(1L);

        verify(documentRepository).existsById(1L);

        verify(documentRepository).deleteById(1L);
    }

    @Test
    void deveConsultarArmazenamento() {


        when(documentRepository.calcularEspacoUtilizado())
                .thenReturn(10485760L);

        when(documentRepository.quantidadeArquivos())
                .thenReturn(3L);

        StorageResponseDTO response = documentService.consultarArmazenamento();


        assertNotNull(response);
        assertEquals(10485760L, response.totalBytes());
        assertEquals("10,00 MB", response.espacoUtilizado());
        assertEquals(3L, response.quantidadeArquivos());

        verify(documentRepository).calcularEspacoUtilizado();
        verify(documentRepository).quantidadeArquivos();
    }

    @Test
    void deveConsultarArmazenamentoQuandoNaoHaArquivos() {

        when(documentRepository.calcularEspacoUtilizado())
                .thenReturn(0L);

        when(documentRepository.quantidadeArquivos())
                .thenReturn(0L);

        StorageResponseDTO response = documentService.consultarArmazenamento();

        assertNotNull(response);
        assertEquals(0L, response.totalBytes());
        assertEquals("0 B", response.espacoUtilizado());
        assertEquals(0L, response.quantidadeArquivos());

        verify(documentRepository).calcularEspacoUtilizado();
        verify(documentRepository).quantidadeArquivos();
    }

    @Test
    void deveLancarExcecaoAoBuscarDocumentoInexistente() {

        when(documentRepository.findById(1L))
                .thenReturn(Optional.empty());


        assertThrows(
                DocumentNotFoundException.class,
                () -> documentService.conteudo(1L)
        );

        verify(documentRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoExcluirDocumentoInexistente() {

        when(documentRepository.existsById(1L))
                .thenReturn(false);


        assertThrows(
                DocumentNotFoundException.class,
                () -> documentService.excluir(1L)
        );

        verify(documentRepository).existsById(1L);

        verify(documentRepository, never()).deleteById(1L);



    }

    @Test
    void deveLancarExcecaoAoListarDocumentosDePastaInexistente() {

        when(folderRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                FolderNotFoundException.class,
                () -> documentService.listarPorPasta(1L)
        );


        verify(folderRepository).existsById(1L);

        verify(documentRepository, never()).findByFolderId(anyLong());





    }










}
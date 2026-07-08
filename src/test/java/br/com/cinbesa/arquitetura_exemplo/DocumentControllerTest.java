package br.com.cinbesa.arquitetura_exemplo;

import br.com.cinbesa.arquitetura_exemplo.controller.DocumentController;
import br.com.cinbesa.arquitetura_exemplo.dto.DocumentContentDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.DocumentResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.StorageResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.exception.DocumentNotFoundException;
import br.com.cinbesa.arquitetura_exemplo.exception.FolderNotFoundException;
import br.com.cinbesa.arquitetura_exemplo.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @Test
    void deveListarTodosDocumentos() throws Exception {

        DocumentResponseDTO doc = new DocumentResponseDTO(
                1L, "curriculo.pdf", "PDF", 1L, "application/pdf", 1024L, LocalDateTime.now()
        );

        when(documentService.listarTodos())
                .thenReturn(List.of(doc));

        mockMvc.perform(get("/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("curriculo.pdf"));

        verify(documentService).listarTodos();
    }

    @Test
    void deveListarDocumentosPorPasta() throws Exception {

        DocumentResponseDTO doc = new DocumentResponseDTO(
                1L, "curriculo.pdf", "PDF", 1L, "application/pdf", 1024L, LocalDateTime.now()
        );

        when(documentService.listarPorPasta(1L))
                .thenReturn(List.of(doc));

        mockMvc.perform(get("/folders/1/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].folderId").value(1));

        verify(documentService).listarPorPasta(1L);
    }

    @Test
    void deveRetornar404AoListarDocumentosDePastaInexistente() throws Exception {

        when(documentService.listarPorPasta(99L))
                .thenThrow(new FolderNotFoundException(99L));

        mockMvc.perform(get("/folders/99/documents"))
                .andExpect(status().isNotFound());

        verify(documentService).listarPorPasta(99L);
    }

    @Test
    void deveFazerUploadDeDocumento() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file", "curriculo.pdf", "application/pdf", "conteudo".getBytes()
        );

        DocumentResponseDTO response = new DocumentResponseDTO(
                1L, "curriculo.pdf", "PDF", 1L, "application/pdf",
                (long) "conteudo".getBytes().length, LocalDateTime.now()
        );

        when(documentService.upload(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(multipart("/folders/1/documents").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("curriculo.pdf"))
                .andExpect(jsonPath("$.tipo").value("PDF"));

        verify(documentService).upload(eq(1L), any());
    }

    @Test
    void deveRetornar404AoFazerUploadEmPastaInexistente() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file", "curriculo.pdf", "application/pdf", "conteudo".getBytes()
        );

        when(documentService.upload(eq(99L), any()))
                .thenThrow(new FolderNotFoundException(99L));

        mockMvc.perform(multipart("/folders/99/documents").file(file))
                .andExpect(status().isNotFound());

        verify(documentService).upload(eq(99L), any());
    }

    @Test
    void deveVisualizarDocumento() throws Exception {

        byte[] conteudo = "conteudo do pdf".getBytes();
        DocumentContentDTO document = new DocumentContentDTO("curriculo.pdf", "application/pdf", conteudo);

        when(documentService.conteudo(1L))
                .thenReturn(document);

        mockMvc.perform(get("/documents/1/view"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("inline")))
                .andExpect(content().bytes(conteudo));

        verify(documentService).conteudo(1L);
    }

    @Test
    void deveBaixarDocumento() throws Exception {

        byte[] conteudo = "conteudo do pdf".getBytes();
        DocumentContentDTO document = new DocumentContentDTO("curriculo.pdf", "application/pdf", conteudo);

        when(documentService.conteudo(1L))
                .thenReturn(document);

        mockMvc.perform(get("/documents/1/download"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment")))
                .andExpect(content().bytes(conteudo));

        verify(documentService).conteudo(1L);
    }

    @Test
    void deveRetornar404AoVisualizarDocumentoInexistente() throws Exception {

        when(documentService.conteudo(99L))
                .thenThrow(new DocumentNotFoundException(99L));

        mockMvc.perform(get("/documents/99/view"))
                .andExpect(status().isNotFound());

        verify(documentService).conteudo(99L);
    }

    @Test
    void deveExcluirDocumento() throws Exception {

        mockMvc.perform(delete("/documents/1"))
                .andExpect(status().isOk());

        verify(documentService).excluir(1L);
    }

    @Test
    void deveRetornar404AoExcluirDocumentoInexistente() throws Exception {

        org.mockito.Mockito.doThrow(new DocumentNotFoundException(99L))
                .when(documentService).excluir(99L);

        mockMvc.perform(delete("/documents/99"))
                .andExpect(status().isNotFound());

        verify(documentService).excluir(99L);
    }

    @Test
    void deveConsultarArmazenamento() throws Exception {

        StorageResponseDTO response = new StorageResponseDTO(10485760L, "10,00 MB", 3L);

        when(documentService.consultarArmazenamento())
                .thenReturn(response);

        mockMvc.perform(get("/documents/storage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBytes").value(10485760))
                .andExpect(jsonPath("$.quantidadeArquivos").value(3));

        verify(documentService).consultarArmazenamento();
    }

}
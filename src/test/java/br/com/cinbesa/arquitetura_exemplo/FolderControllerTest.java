package br.com.cinbesa.arquitetura_exemplo;

import br.com.cinbesa.arquitetura_exemplo.controller.FolderController;
import br.com.cinbesa.arquitetura_exemplo.dto.FolderRequestDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.FolderResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.exception.FolderNotFoundException;
import br.com.cinbesa.arquitetura_exemplo.service.FolderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(FolderController.class)
class FolderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private tools.jackson.databind.ObjectMapper objectMapper;

    @MockitoBean
    private FolderService folderService;

    @Test
    void deveCriarPasta() throws Exception {

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Arquivos",
                        null
                );

        FolderResponseDTO response =
                new FolderResponseDTO(
                        1L,
                        "Arquivos",
                        LocalDateTime.now(),
                        null,
                        List.of()
                );

        when(folderService.criar(any(FolderRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/folders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Arquivos"));

        verify(folderService).criar(any(FolderRequestDTO.class));
    }

    @Test
    void deveRetornar400QuandoNomeInvalido() throws Exception {

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "",
                        null
                );

        mockMvc.perform(post("/folders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(folderService, never()).criar(any(FolderRequestDTO.class));
    }

    @Test
    void deveCriarSubpasta() throws Exception {

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Imagens",
                        null
                );

        FolderResponseDTO response =
                new FolderResponseDTO(
                        2L,
                        "Imagens",
                        LocalDateTime.now(),
                        1L,
                        List.of()
                );

        when(folderService.criarSubpasta(eq(1L), any(FolderRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/folders/1/subfolders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.parentId").value(1));

        verify(folderService).criarSubpasta(eq(1L), any(FolderRequestDTO.class));
    }

    @Test
    void deveListarPastas() throws Exception {

        FolderResponseDTO pasta =
                new FolderResponseDTO(
                        1L,
                        "Arquivos",
                        LocalDateTime.now(),
                        null,
                        List.of()
                );

        when(folderService.listar(null))
                .thenReturn(List.of(pasta));

        mockMvc.perform(get("/folders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Arquivos"));

        verify(folderService).listar(null);
    }

    @Test
    void deveBuscarPastaPorId() throws Exception {

        FolderResponseDTO response =
                new FolderResponseDTO(
                        1L,
                        "Documentos",
                        LocalDateTime.now(),
                        null,
                        List.of()
                );

        when(folderService.buscaPorId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/folders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Documentos"));

        verify(folderService).buscaPorId(1L);
    }

    @Test
    void deveRetornar404QuandoPastaNaoEncontrada() throws Exception {

        when(folderService.buscaPorId(99L))
                .thenThrow(new FolderNotFoundException(99L));

        mockMvc.perform(get("/folders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(folderService).buscaPorId(99L);
    }

    @Test
    void deveAtualizarPasta() throws Exception {

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Arquivos Renomeados",
                        null
                );

        FolderResponseDTO response =
                new FolderResponseDTO(
                        1L,
                        "Arquivos Renomeados",
                        LocalDateTime.now(),
                        null,
                        List.of()
                );

        when(folderService.atualizar(eq(1L), any(FolderRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/folders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Arquivos Renomeados"));

        verify(folderService).atualizar(eq(1L), any(FolderRequestDTO.class));
    }

    @Test
    void deveRetornar404AoAtualizarPastaInexistente() throws Exception {

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Arquivos",
                        null
                );

        when(folderService.atualizar(eq(99L), any(FolderRequestDTO.class)))
                .thenThrow(new FolderNotFoundException(99L));

        mockMvc.perform(patch("/folders/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(folderService).atualizar(eq(99L), any(FolderRequestDTO.class));
    }

    @Test
    void deveExcluirPasta() throws Exception {

        mockMvc.perform(delete("/folders/1"))
                .andExpect(status().isOk());

        verify(folderService).excluir(1L);
    }

    @Test
    void deveRetornar404AoExcluirPastaInexistente() throws Exception {

        org.mockito.Mockito.doThrow(new FolderNotFoundException(99L))
                .when(folderService).excluir(99L);

        mockMvc.perform(delete("/folders/99"))
                .andExpect(status().isNotFound());

        verify(folderService).excluir(99L);
    }

}
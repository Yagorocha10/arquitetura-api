package br.com.cinbesa.arquitetura_exemplo;

import br.com.cinbesa.arquitetura_exemplo.dto.FolderRequestDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.FolderResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.entity.Folder;
import br.com.cinbesa.arquitetura_exemplo.exception.FolderNotFoundException;
import br.com.cinbesa.arquitetura_exemplo.repository.DocumentRepository;
import br.com.cinbesa.arquitetura_exemplo.repository.FolderRepository;
import br.com.cinbesa.arquitetura_exemplo.service.FolderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FolderServiceTest {

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private FolderService folderService;


    @Test
    void deveCriarPastaRaiz() {

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Documentos",
                        null
                );

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .dataCriacao(LocalDateTime.now())
                .build();

        when(folderRepository.save(any(Folder.class))).thenReturn(folder);

        when(folderRepository.findByParentId(anyLong()))
                .thenReturn(List.of());

        FolderResponseDTO response =
                folderService.criar(request);

        assertNotNull(response);

        assertEquals(1L, response.id());

        assertEquals("Documentos", response.nome());

        verify(folderRepository, times(1))
                .save(any(Folder.class));

        verify(folderRepository, times(1))
                .findByParentId(anyLong());


    }

    @Test
    void deveCriarPastaComParentIdInformado() {

        Folder parent = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .build();

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Contratos",
                        1L
                );

        Folder novaPasta = Folder.builder()
                .id(2L)
                .nome("Contratos")
                .parent(parent)
                .build();

        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(parent));

        when(folderRepository.save(any(Folder.class)))
                .thenReturn(novaPasta);

        when(folderRepository.findByParentId(2L))
                .thenReturn(List.of());

        FolderResponseDTO response =
                folderService.criar(request);

        assertNotNull(response);

        assertEquals(2L, response.id());

        assertEquals(1L, response.parentId());

        verify(folderRepository).findById(1L);

        verify(folderRepository).save(any(Folder.class));
    }

    @Test
    void deveLancarExcecaoAoCriarPastaComParentIdInexistente() {

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Contratos",
                        99L
                );

        when(folderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                FolderNotFoundException.class,
                () -> folderService.criar(request)
        );

        verify(folderRepository).findById(99L);

        verify(folderRepository, never()).save(any(Folder.class));
    }

    @Test
    void deveCriarSubpasta() {


        Folder pastaPai = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .dataCriacao(LocalDateTime.now())
                .build();

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Imagens",
                        null
                );

        Folder subpasta = Folder.builder()
                .id(2L)
                .nome("Imagens")
                .parent(pastaPai)
                .dataCriacao(LocalDateTime.now())
                .build();


        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(pastaPai));

        when(folderRepository.save(any(Folder.class)))
                .thenReturn(subpasta);

        when(folderRepository.findByParentId(2L))
                .thenReturn(List.of());

        FolderResponseDTO response =
                folderService.criarSubpasta(1L, request);

        assertNotNull(response);

        assertEquals(2L, response.id());

        assertEquals("Imagens", response.nome());

        assertEquals(1L, response.parentId());

        verify(folderRepository).findById(1L);

        verify(folderRepository).save(any(Folder.class));
    }

    @Test
    void deveLancarExcecaoQuandoPastaPaiNaoExistir() {

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Imagens",
                        null
                );

        when(folderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                FolderNotFoundException.class,
                () -> folderService.criarSubpasta(99L, request)
        );

        verify(folderRepository)
                .findById(99L);

        verify(folderRepository, never())
                .save(any(Folder.class));
    }

    @Test
    void deveListarPastasRaiz() {

        Folder pasta1 = Folder.builder()
                .id(1l)
                .nome("Documentos")
                .dataCriacao(LocalDateTime.now())
                .build();

        Folder pasta2 = Folder.builder()
                .id(2L)
                .nome("Fotos")
                .dataCriacao(LocalDateTime.now())
                .build();

        when(folderRepository.findByParentIsNull())
                .thenReturn(List.of(pasta1, pasta2));

        when(folderRepository.findByParentId(anyLong()))
                .thenReturn(List.of());

        List<FolderResponseDTO> response =
                folderService.listar(null);

        assertNotNull(response);

        assertEquals(2, response.size());

        assertEquals("Documentos", response.get(0).nome());

        assertEquals("Fotos", response.get(1).nome());

        verify(folderRepository)
                .findByParentIsNull();
    }

    @Test
    void deveListarSubpastasDeUmaPastaEspecifica() {

        Folder pai = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .build();

        Folder sub1 = Folder.builder()
                .id(2L)
                .nome("Contratos")
                .parent(pai)
                .build();

        when(folderRepository.findByParentId(1L))
                .thenReturn(List.of(sub1));

        when(folderRepository.findByParentId(2L))
                .thenReturn(List.of());

        List<FolderResponseDTO> response =
                folderService.listar(1L);

        assertNotNull(response);

        assertEquals(1, response.size());

        assertEquals("Contratos", response.get(0).nome());

        assertEquals(1L, response.get(0).parentId());

        verify(folderRepository, never())
                .findByParentIsNull();

        verify(folderRepository).findByParentId(1L);

        verify(folderRepository).findByParentId(2L);
    }

    @Test
    void deveBuscarPastaPorId() {

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .dataCriacao(LocalDateTime.now())
                .build();


        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(folder));

        when(folderRepository.findByParentId(1L))
                .thenReturn(List.of());


        FolderResponseDTO response =
                folderService.buscaPorId(1L);

        assertNotNull(response);

        assertEquals(1l, response.id());

        assertEquals("Documentos", response.nome());

        assertNull(response.parentId());

        verify(folderRepository)
                .findById(1L);

        verify(folderRepository)
                .findByParentId(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarPastaPorIdInexistente() {

        when(folderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                FolderNotFoundException.class,
                () -> folderService.buscaPorId(99L)
        );

        verify(folderRepository).findById(99L);

        verify(folderRepository, never()).findByParentId(anyLong());
    }

    @Test
    void deveAtualizarNomeDaPasta() {

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .dataCriacao(LocalDateTime.now())
                .build();

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Arquivos",
                        null
                );

        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(folder));

        when(folderRepository.findByParentId(1L))
                .thenReturn(List.of());

        FolderResponseDTO response =
                folderService.atualizar(1L, request);

        assertNotNull(response);

        assertEquals(1L, response.id());

        assertEquals("Arquivos", response.nome());

        assertNull(response.parentId());

        assertEquals("Arquivos", folder.getNome());

        verify(folderRepository)
                .findById(1L);

        verify(folderRepository)
                .findByParentId(1L);

        verify(folderRepository, never())
                .save(any(Folder.class));

    }

    @Test
    void deveLancarExcecaoAoAtualizarPastaInexistente() {

        FolderRequestDTO request =
                new FolderRequestDTO(
                        "Arquivos",
                        null
                );

        when(folderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                FolderNotFoundException.class,
                () -> folderService.atualizar(99L, request)
        );

        verify(folderRepository).findById(99L);

        verify(folderRepository, never()).findByParentId(anyLong());
    }

    @Test
    void deveExcluirPastaSemSubpastas() {

        Folder folder = Folder.builder()
                .id(1L)
                .nome("Documentos")
                .dataCriacao(LocalDateTime.now())
                .build();

        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(folder));

        when(folderRepository.findByParentId(1L))
                .thenReturn(List.of());

        folderService.excluir(1L);

        verify(folderRepository)
                .findById(1L);

        verify(folderRepository)
                .findByParentId(1L);

        verify(folderRepository)
                .delete(folder);


    }

    @Test
    void deveExcluirPastaRecursivamente() {

        Folder pai = Folder.builder()
                .id(1L)
                .nome("Projetos")
                .build();

        Folder filha = Folder.builder()
                .id(2L)
                .nome("Java")
                .parent(pai)
                .build();


        when(folderRepository.findById(1L))
                .thenReturn(Optional.of(pai));

        when(folderRepository.findByParentId(1L))
                .thenReturn(List.of(filha));

        when(folderRepository.findByParentId(2L))
                .thenReturn(List.of());

        folderService.excluir(1L);

        InOrder inOrder = inOrder(documentRepository, folderRepository);

        verify(documentRepository).deleteByFolderId(2L);

        verify(folderRepository).delete(filha);

        verify(documentRepository).deleteByFolderId(1L);

        verify(folderRepository).delete(pai);


    }

    @Test
    void deveLancarExcecaoAoExcluirPastaInexistente() {

        when(folderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                FolderNotFoundException.class,
                () -> folderService.excluir(99L)
        );

        verify(folderRepository).findById(99L);

        verify(folderRepository, never()).findByParentId(anyLong());

        verify(documentRepository, never()).deleteByFolderId(anyLong());

        verify(folderRepository, never()).delete(any(Folder.class));
    }




}
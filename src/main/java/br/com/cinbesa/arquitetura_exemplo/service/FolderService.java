package br.com.cinbesa.arquitetura_exemplo.service;

import br.com.cinbesa.arquitetura_exemplo.dto.FolderRequestDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.FolderResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.entity.Folder;
import br.com.cinbesa.arquitetura_exemplo.exception.FolderNotFoundException;
import br.com.cinbesa.arquitetura_exemplo.repository.DocumentRepository;
import br.com.cinbesa.arquitetura_exemplo.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;
    private final DocumentRepository documentRepository;

    public FolderResponseDTO criar(FolderRequestDTO folderRequestDTO) {
        Folder parent = null;

        if (folderRequestDTO.parentId() != null) {
            parent = buscarEntidade(folderRequestDTO.parentId());
        }

        Folder folder = criarEntidade(folderRequestDTO.nome(), parent);

        return toResponse(folder);
    }

    public FolderResponseDTO criarSubpasta(Long parentId, FolderRequestDTO folderRequestDTO) {
        Folder parent = buscarEntidade(parentId);
        Folder folder = criarEntidade(folderRequestDTO.nome(), parent);

        return toResponse(folder);
    }

    public List<FolderResponseDTO> listar(Long parentId) {
        List<Folder> folders = parentId == null
                ? folderRepository.findByParentIsNull()
                : folderRepository.findByParentId(parentId);

        return folders.stream()
                .map(this::toResponse)
                .toList();
    }

    public FolderResponseDTO buscaPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Transactional
    public FolderResponseDTO atualizar(Long id, FolderRequestDTO folderRequestDTO) {
        Folder folder = buscarEntidade(id);
        folder.setNome(folderRequestDTO.nome());

        return toResponse(folder);
    }

    @Transactional
    public void excluir(Long id) {
        Folder folder = buscarEntidade(id);
        excluirRecursivamente(folder);
    }

    private Folder criarEntidade(String nome, Folder parent) {
        Folder folder = Folder.builder()
                .nome(nome)
                .dataCriacao(LocalDateTime.now())
                .parent(parent)
                .build();

        return folderRepository.save(folder);
    }

    private Folder buscarEntidade(Long id) {
        return folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));
    }

    private void excluirRecursivamente(Folder folder) {
        folderRepository.findByParentId(folder.getId())
                .forEach(this::excluirRecursivamente);

        documentRepository.deleteByFolderId(folder.getId());
        folderRepository.delete(folder);
    }

    private FolderResponseDTO toResponse(Folder folder) {
        Long parentId = folder.getParent() == null ? null : folder.getParent().getId();
        List<FolderResponseDTO> children = folderRepository.findByParentId(folder.getId())
                .stream()
                .map(this::toResponse)
                .toList();

        return new FolderResponseDTO(
                folder.getId(),
                folder.getNome(),
                folder.getDataCriacao(),
                parentId,
                children
        );
    }
}

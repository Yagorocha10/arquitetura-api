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

        Folder folder = Folder.builder()
                .nome(folderRequestDTO.nome())
                .dataCriacao(LocalDateTime.now())
                .build();

        folder = folderRepository.save(folder);

        return new FolderResponseDTO(
                folder.getId(),
                folder.getNome(),
                folder.getDataCriacao()
        );


    }

    public List<FolderResponseDTO> listar() {

        return folderRepository.findAll()
                .stream()
                .map(folder -> new FolderResponseDTO(
                        folder.getId(),
                        folder.getNome(),
                        folder.getDataCriacao()

                )).toList();

    }


    public FolderResponseDTO buscaPorId(Long id) {

        Folder  folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));

        return new FolderResponseDTO(
                folder.getId(),
                folder.getNome(),
                folder.getDataCriacao()
        );

    }

    @Transactional
    public void excluir(Long id) {

        if(!folderRepository.existsById(id)) {
            throw new FolderNotFoundException(id);
        }

        documentRepository.deleteByFolderId(id);
        folderRepository.deleteById(id);
    }





}

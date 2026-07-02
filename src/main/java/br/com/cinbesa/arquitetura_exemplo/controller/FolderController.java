package br.com.cinbesa.arquitetura_exemplo.controller;

import br.com.cinbesa.arquitetura_exemplo.dto.FolderRequestDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.FolderResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public FolderResponseDTO criar(@RequestBody @Valid FolderRequestDTO folderRequestDTO) {
        return folderService.criar(folderRequestDTO);
    }

    @PostMapping("/{id}/subfolders")
    public FolderResponseDTO criarSubpasta(
            @PathVariable Long id,
            @RequestBody @Valid FolderRequestDTO folderRequestDTO
    ) {
        return folderService.criarSubpasta(id, folderRequestDTO);
    }


    @GetMapping
    public List<FolderResponseDTO> listar(@RequestParam(required = false) Long parentId) {
        return folderService.listar(parentId);
    }

    @GetMapping("/{id}")
    public FolderResponseDTO buscaPorId(@PathVariable Long id) {
        return folderService.buscaPorId(id);
    }

    @PatchMapping("/{id}")
    public FolderResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody @Valid FolderRequestDTO folderRequestDTO
    ) {
        return folderService.atualizar(id, folderRequestDTO);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        folderService.excluir(id);
    }


}

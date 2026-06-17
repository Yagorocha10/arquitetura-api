package br.com.cinbesa.arquitetura_exemplo.controller;

import br.com.cinbesa.arquitetura_exemplo.dto.FolderRequestDTO;
import br.com.cinbesa.arquitetura_exemplo.dto.FolderResponseDTO;
import br.com.cinbesa.arquitetura_exemplo.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public List<FolderResponseDTO> listar() {
        return folderService.listar();
    }

    @GetMapping("/{id}")
    public FolderResponseDTO buscaPorId(@PathVariable Long id) {
        return folderService.buscaPorId(id);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {

        folderService.excluir(id);
    }


}

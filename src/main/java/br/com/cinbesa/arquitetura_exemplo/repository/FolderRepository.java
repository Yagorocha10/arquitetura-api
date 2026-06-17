package br.com.cinbesa.arquitetura_exemplo.repository;

import br.com.cinbesa.arquitetura_exemplo.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}

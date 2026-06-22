package br.com.cinbesa.arquitetura_exemplo.repository;

import br.com.cinbesa.arquitetura_exemplo.entity.StoredDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<StoredDocument, Long> {

    List<StoredDocument> findByFolderId(Long folderId);

    void deleteByFolderId(Long folderId);
}

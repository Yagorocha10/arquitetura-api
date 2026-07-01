package br.com.cinbesa.arquitetura_exemplo.repository;

import br.com.cinbesa.arquitetura_exemplo.entity.StoredDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentRepository extends JpaRepository<StoredDocument, Long> {

    List<StoredDocument> findByFolderId(Long folderId);

    void deleteByFolderId(Long folderId);

    @Query("select coalesce(sum(document.tamanho), 0) from StoredDocument document")
    long sumTotalSizeBytes();
}

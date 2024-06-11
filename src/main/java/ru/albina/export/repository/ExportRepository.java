package ru.albina.export.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.albina.export.domain.ExportEntity;

import java.util.UUID;

@Repository
public interface ExportRepository extends JpaRepository<ExportEntity, UUID> {
}

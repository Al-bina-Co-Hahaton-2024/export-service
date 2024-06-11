package ru.albina.export.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.albina.export.domain.ExportEntity;
import ru.albina.export.domain.ExportStatus;
import ru.albina.export.domain.ExportType;
import ru.albina.export.exception.EntityNotFoundException;
import ru.albina.export.repository.ExportRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ExportRepository exportRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExportEntity export(ExportType type) {
        return this.exportRepository.save(
                new ExportEntity()
                        .setId(UUID.randomUUID())
                        .setType(type)
                        .setStatus(ExportStatus.PROCESSING)
        );
    }

    @Transactional(readOnly = true)
    public ExportEntity getExport(UUID id) {
        return this.exportRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find export with id: " + id)
        );
    }

}

package ru.albina.export.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.albina.export.domain.ExportType;
import ru.albina.export.dto.response.ExportDto;
import ru.albina.export.mapper.ExportMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExportDtoService {

    private final ExportService exportService;

    private final ExportMapper exportMapper;

    public ExportDto create(ExportType exportType) {
        return this.exportMapper.to(
                this.exportService.export(exportType)
        );
    }

    @Transactional(readOnly = true)
    public ExportDto getById(UUID id) {
        return this.exportMapper.to(this.exportService.getExport(id));
    }

}

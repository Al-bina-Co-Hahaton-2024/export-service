package ru.albina.export.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.albina.export.domain.ExportType;
import ru.albina.export.dto.response.ExportDto;
import ru.albina.export.mapper.ExportMapper;

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

}

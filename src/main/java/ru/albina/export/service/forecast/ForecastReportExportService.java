package ru.albina.export.service.forecast;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.albina.export.domain.ExportType;
import ru.albina.export.dto.response.ExportDto;
import ru.albina.export.service.ExportDtoService;

@Service
@RequiredArgsConstructor
public class ForecastReportExportService {

    private final ForecastReportExportGenerator generator;

    private final ExportDtoService exportDtoService;

    public ExportDto generate(int year) {
        final var export = this.exportDtoService.create(ExportType.FORECAST_MODALITY);
        this.generator.generate(export.id(), year);
        return export;
    }
}

package ru.albina.export.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.albina.export.domain.ExportType;
import ru.albina.export.dto.response.ExportDto;
import ru.albina.export.service.doctor.DoctorReportCardGenerator;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DoctorReportCardExportService {

    private final DoctorReportCardGenerator generator;

    private final ExportDtoService exportDtoService;

    public ExportDto generate(LocalDate localDate) {
        final var export = this.exportDtoService.create(ExportType.DOCTOR_REPORT_CARD);
        this.generator.generate(export.id(), localDate);
        return export;
    }
}

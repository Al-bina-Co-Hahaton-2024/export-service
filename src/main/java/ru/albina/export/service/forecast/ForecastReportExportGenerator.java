package ru.albina.export.service.forecast;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.albina.export.domain.ExportStatus;
import ru.albina.export.service.ExportService;
import ru.albina.export.service.s3.S3Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastReportExportGenerator {

    private final ExportService exportService;

    private final S3Service s3Service;

    private final ForecastReportGenerator forecastReportGenerator;

    private final ForecastReportFileGenerator forecastReportFileGenerator;

    @Async
    @Transactional
    public void generate(UUID exportId, int year) {
        final var export = this.exportService.getExport(exportId);
        try {
            export
                    .setStatus(ExportStatus.SUCCESSFUL)
                    .setLink(
                            this.s3Service.uploadFile(
                                    this.forecastReportFileGenerator.generateFile(this.forecastReportGenerator.generate(year)),
                                    "xlsx"
                            )
                    );
        } catch (Exception e) {
            export.setStatus(ExportStatus.ERROR);
            log.error("Error during forecast error export", e);
        }

    }
}

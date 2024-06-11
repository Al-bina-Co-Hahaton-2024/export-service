package ru.albina.export.service.doctor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.albina.export.domain.ExportStatus;
import ru.albina.export.service.ExportService;
import ru.albina.export.service.s3.S3Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorReportCardGenerator {

    private final ExportService exportService;

    private final S3Service s3Service;

    private final DoctorReportCardFileGenerator doctorReportCardFileGenerator;


    @Async
    @Transactional
    public void generate(UUID exportId, LocalDate targetDate) {
        final var export = this.exportService.getExport(exportId);
        try {

            export
                    .setStatus(ExportStatus.SUCCESSFUL)
                    .setLink(
                            this.s3Service.uploadFile(
                                    this.doctorReportCardFileGenerator.generate(targetDate), "xlsx"
                            )
                    );
        } catch (Exception e) {
            export.setStatus(ExportStatus.ERROR);
            log.error("Error during doctor error export", e);
        }
    }
}

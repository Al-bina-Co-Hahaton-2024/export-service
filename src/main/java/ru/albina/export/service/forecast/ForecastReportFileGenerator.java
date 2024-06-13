package ru.albina.export.service.forecast;

import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Service;
import ru.albina.export.component.FileComponent;
import ru.albina.export.dto.medical.Modality;
import ru.albina.export.dto.reference.TypeModality;
import ru.albina.export.mapper.ModalityMapper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ForecastReportFileGenerator {

    private static final List<String> HEADERS = List.of(
            "Год",
            "Номер недели",
            "Денситометр прогноз",
            "КТ(без КУ) прогноз",
            "КТ с КУ 1 зона прогноз",
            "КТ с КУ 2 и более зон прогноз",
            "ММГ прогноз",
            "МРТ (без КУ) прогноз",
            "МРТ с КУ 1 зона прогноз",
            "МРТ с КУ 2 и более зон прогноз",
            "РГ прогноз",
            "Флюорограф прогноз"
    );

    private static final List<Function<WorkloadWeek, Long>> PARSER = List.of(
            (w) -> Long.valueOf(w.getYear()),
            (w) -> Long.valueOf(w.getWeek()),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.DENSITOMETER, TypeModality.DEFAULT), 0L),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.KT, TypeModality.DEFAULT), 0L),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.KT, TypeModality.U), 0L),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.KT, TypeModality.U2), 0L),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.MMG, TypeModality.DEFAULT), 0L),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.MRT, TypeModality.DEFAULT), 0L),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.MRT, TypeModality.U), 0L),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.MRT, TypeModality.U2), 0L),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.RG, TypeModality.DEFAULT), 0L),
            (w) -> w.getModalities().getOrDefault(ModalityMapper.to(Modality.FLG, TypeModality.DEFAULT), 0L)
    );

    private final FileComponent fileComponent;

    public File generateFile(List<WorkloadWeek> generate) {
        final var tempFile = this.fileComponent.getTempFile();

        try (
                OutputStream os = Files.newOutputStream(tempFile.toPath());
                Workbook wb = new Workbook(os, "Albina&Co - Forecast", "1.0")
        ) {
            Worksheet ws = wb.newWorksheet("Прогноз");

            var line = 0;

            for (int i = 0; i < HEADERS.size(); i++) {
                ws.value(line, i, HEADERS.get(i));
                ws.style(line, i)
                        .horizontalAlignment("center")
                        .verticalAlignment("center")
                        .fontSize(12)
                        .fontName("Times New Roman")
                        .wrapText(true)
                        .set();
            }
            ws.rowHeight(line, 45);
            line++;
            for (WorkloadWeek workloadWeek : generate) {
                for (int i = 0; i < PARSER.size(); i++) {
                    ws.value(line, i, PARSER.get(i).apply(workloadWeek));
                }
                line++;
            }
            ws.freezePane(1, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tempFile;
    }
}

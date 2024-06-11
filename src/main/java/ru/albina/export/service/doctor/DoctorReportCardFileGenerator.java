package ru.albina.export.service.doctor;

import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Service;
import ru.albina.export.client.MedicalClient;
import ru.albina.export.client.PlannerClient;
import ru.albina.export.component.FileComponent;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DoctorReportCardFileGenerator {

    private static final String PLACEHOLDER = "{MONTH}";

    private static final int EMPLOYEE_SPACE = 3;

    private static final List<String> HEADERS = List.of(
            String.join(",\n", List.of("Фамилия", "Имя", "Отчество")),
            "Модальность",
            "Дополнительные модальности",
            "Ставка",
            "Таб. №",
            "",
            PLACEHOLDER,
            "Норма часов по графику",
            "Норма часов за полный месяц",
            "Дата",
            "Подпись"
    );

    private final MedicalClient medicalClient;
    private final PlannerClient plannerClient;
    private final FileComponent fileComponent;

    public File generate(LocalDate targetDate) {
        final var tempFile = this.fileComponent.getTempFile();

        try (
                OutputStream os = Files.newOutputStream(tempFile.toPath());
                Workbook wb = new Workbook(os, "Albina&Co - DoctorReportCard", "1.0")
        ) {
            Worksheet ws = wb.newWorksheet("Отчет");
            this.fillTemplate(ws, targetDate);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tempFile;
    }


    private void fillTemplate(Worksheet ws, LocalDate targetDate) {
        int index = 0;
        for (String headerText : HEADERS) {

            if (headerText.equals(PLACEHOLDER)) {
                final var length = targetDate.lengthOfMonth();
                ws.range(0, index, 0, length + 2).merge();
                ws.value(0, index, String.format("Число месяцев (%s)", targetDate));
                ws.style(0, index)
                        .horizontalAlignment("center")
                        .verticalAlignment("center")
                        .fontSize(12)
                        .fontName("Times New Roman")
                        .wrapText(true)
                        .set();

                final var titles = IntStream.range(1, targetDate.lengthOfMonth() + 1).mapToObj(Objects::toString).collect(Collectors.toList());
                titles.add(15, "Итого за 1 пол. месяца");
                titles.add("Итого за 2 пол. месяца");

                for (String title : titles) {
                    ws.value(1, index, title);
                    ws.style(1, index)
                            .horizontalAlignment("center")
                            .verticalAlignment("center")
                            .fontSize(12)
                            .fontName("Times New Roman")
                            .wrapText(true)
                            .set();
                    index++;
                }

                continue;
            }

            ws.range(0, index, 1, index).merge();
            ws.value(0, index, headerText);
            ws.style(0, index)
                    .horizontalAlignment("center")
                    .verticalAlignment("center")
                    .fontSize(12)
                    .fontName("Times New Roman")
                    .wrapText(true)
                    .set();
            ws.width(index, 30);
            index++;
        }

        ws.rowHeight(0, 70);
        ws.rowHeight(1, 70);


        final var doctors = this.medicalClient.getDoctors();
        final var planner = this.plannerClient.getScheduler(targetDate);

        var top = 2;
        for (String s : List.of("Врач1", "Врач2", "Врач3")) {
            generate(ws, targetDate, top, s);
            top += EMPLOYEE_SPACE + 1;
        }

        ws.freezePane(1, 2);
    }


    private void generate(Worksheet ws, LocalDate now, int line, String s) {

        final var data = List.of(s, "РГ", "РК", "1", "1020203");
        int i = 0;
        for (; i < data.size(); i++) {
            ws.range(line, i, EMPLOYEE_SPACE + line, i).merge();
            ws.value(line, i, data.get(i));
            ws.style(line, i)
                    .horizontalAlignment("center")
                    .verticalAlignment("center")
                    .wrapText(true)
                    .set();
        }

        final var templateHours = List.of("с", "до", "перерыв", "отраб.");
        for (int j = 0; j < templateHours.size(); j++) {
            ws.value(line + j, i, templateHours.get(j));
            ws.style(line + j, i)
                    .horizontalAlignment("center")
                    .verticalAlignment("center")
                    .wrapText(true)
                    .set();
        }
        i++;

        final var hours = IntStream.range(1, now.lengthOfMonth() + 1).mapToObj(v -> List.of("8:00", "20:30", "30", "12")).collect(Collectors.toList());
        hours.add(15, List.of("84"));
        hours.add(List.of("72"));

        hours.add(List.of("155"));
        hours.add(List.of("155"));

        hours.add(List.of(""));
        hours.add(List.of(""));

        for (List<String> hour : hours) {
            for (int j = 0; j < hour.size(); j++) {
                ws.value(line + j, i, hour.get(j));
                ws.style(line + j, i)
                        .horizontalAlignment("center")
                        .verticalAlignment("center")
                        .wrapText(true)
                        .set();
                if (hour.size() == 1) {
                    ws.range(line, i, EMPLOYEE_SPACE + line, i).merge();
                    break;
                }
            }
            i++;
        }
    }
}

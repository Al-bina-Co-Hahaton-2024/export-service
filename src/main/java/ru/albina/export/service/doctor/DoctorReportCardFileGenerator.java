package ru.albina.export.service.doctor;

import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.*;
import org.springframework.stereotype.Service;
import ru.albina.export.client.MedicalClient;
import ru.albina.export.client.PlannerClient;
import ru.albina.export.client.ReferenceClient;
import ru.albina.export.client.UserClient;
import ru.albina.export.component.FileComponent;
import ru.albina.export.dto.medical.Doctor;
import ru.albina.export.dto.schedule.DayWorkSchedule;
import ru.albina.export.dto.schedule.DoctorLoad;
import ru.albina.export.dto.user.UserDto;
import ru.albina.export.dto.user.UserFullName;
import ru.albina.export.mapper.MedicalMapper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DoctorReportCardFileGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

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
    private final UserClient userClient;
    private final ReferenceClient referenceClient;

    private final FileComponent fileComponent;

    private final MedicalMapper medicalMapper;

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
                this.headerStyle(ws.style(0, index));

                final var titles = IntStream.range(1, targetDate.lengthOfMonth() + 1).mapToObj(Objects::toString).collect(Collectors.toList());
                titles.add(15, "Итого за 1 пол. месяца");
                titles.add("Итого за 2 пол. месяца");

                for (String title : titles) {
                    ws.value(1, index, title);
                    this.headerStyle(ws.style(1, index));
                    index++;
                }

                continue;
            }

            ws.range(0, index, 1, index).merge();
            ws.value(0, index, headerText);
            this.headerStyle(ws.style(0, index));
            ws.width(index, 8);
            index++;
        }

        ws.rowHeight(0, 70);
        ws.rowHeight(1, 70);


        final var doctors = this.medicalClient.getDoctors();
        final var users = this.userClient.getUsers(doctors.stream().map(Doctor::getId).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(UserDto::getId, UserDto::getFullName));
        final var planner = this.plannerClient.getScheduler(targetDate);

        final var hours = this.referenceClient.getHours(targetDate.getYear(), targetDate.getMonthValue());


        var top = 2;
        for (final var doctor : doctors) {
            this.generate(ws,
                    top,
                    targetDate,
                    hours,
                    users.getOrDefault(doctor.getId(), UserFullName.builder().last("Er").first("ro").last("r").build()),
                    doctor,
                    planner.stream()
                            .filter(dayWorkSchedule -> dayWorkSchedule.getDoctors().stream().anyMatch(doctorLoad -> doctorLoad.getDoctorId().equals(doctor.getId())))
                            .collect(Collectors.toMap(
                                            DayWorkSchedule::getDate,
                                            v -> v.getDoctors().stream().filter(doctorLoad -> doctorLoad.getDoctorId().equals(doctor.getId())).findFirst().orElseThrow()
                                    )
                            )
            );
            top += EMPLOYEE_SPACE + 1;
        }

        ws.freezePane(1, 2);
    }

    private void headerStyle(StyleSetter style) {
        style
                .horizontalAlignment("center")
                .verticalAlignment("center")
                .fontSize(12)
                .fontName("Times New Roman")
                .wrapText(true)
                .set();
    }


    private void generate(Worksheet ws, int line, LocalDate now, double hoursForMonth, UserFullName userFullName, Doctor doctor, Map<LocalDate, DoctorLoad> dayWorkDoctors) {

        final var data = List.of(
                String.format("%s %s %s", userFullName.getLast(), userFullName.getFirst(), userFullName.getMiddle()),
                this.medicalMapper.modality(doctor.getModality()),
                this.medicalMapper.modality(doctor.getOptionalModality()),
                doctor.getRate() + "",
                doctor.getServiceNumber() + ""
        );
        int i = 0;
        for (; i < data.size(); i++) {
            ws.range(line, i, EMPLOYEE_SPACE + line, i).merge();
            ws.value(line, i, data.get(i));
            ws.style(line, i)
                    .horizontalAlignment("center")
                    .borderStyle(BorderSide.BOTTOM, BorderStyle.THIN)
                    .borderStyle(BorderSide.TOP, BorderStyle.THIN)
                    .verticalAlignment("center")
                    .wrapText(true)
                    .set();
        }

        final var templateHours = List.of("с", "до", "перерыв", "отраб.");
        for (int j = 0; j < templateHours.size(); j++) {
            ws.value(line + j, i, templateHours.get(j));
            this.regularStyle(ws.style(line + j, i));
        }
        ws.style(line, i)
                .borderStyle(BorderSide.TOP, BorderStyle.THIN)
                .set();
        ws.style(line + EMPLOYEE_SPACE - 1, i)
                .borderStyle(BorderSide.BOTTOM, BorderStyle.THIN)
                .set();
        i++;

        final var hours = IntStream.range(1, now.lengthOfMonth() + 1)
                .mapToObj(now::withDayOfMonth)
                .map(date -> {
                    final var load = dayWorkDoctors.get(date);
                    if (load != null) {
                        final var doubleHours = Optional.ofNullable(load.getTakenHours()).orElse(0d) + Optional.ofNullable(load.getTakenExtraHours()).orElse(0d);
                        final var doctorHours = this.time(doubleHours);
                        return List.of(
                                Optional.ofNullable(doctor.getStartWorkDay()).map(v -> v.format(DATE_TIME_FORMATTER)).orElse(""),
                                Optional.ofNullable(doctor.getStartWorkDay()).map(v -> v.plus(doctorHours).format(DATE_TIME_FORMATTER)).orElse(""),
                                ((doubleHours < 6) ? 0 : (doubleHours <= 8) ? 30 : 1) + "",
                                this.format(doctorHours)
                        );
                    } else {
                        return new ArrayList<String>();
                    }
                })
                .collect(Collectors.toList());
        hours.add(15, this.calculateHours(now, dayWorkDoctors, 1, 15));
        hours.add(this.calculateHours(now, dayWorkDoctors, 16, now.lengthOfMonth()));

        hours.add(this.calculateHours(now, dayWorkDoctors, 1, now.lengthOfMonth()));
        hours.add(List.of("" + hoursForMonth));

        hours.add(List.of(""));
        hours.add(List.of(""));

        for (List<String> hour : hours) {
            for (int j = 0; j < hour.size(); j++) {
                ws.value(line + j, i, hour.get(j));
                this.regularStyle(ws.style(line + j, i));

                if (hour.size() == 1) {
                    ws.range(line, i, EMPLOYEE_SPACE + line, i).merge();
                    break;
                }
            }
//            ws.style(line, i)
//                    .borderStyle(BorderSide.TOP, BorderStyle.THIN)
//                    .set();
            ws.style(line + EMPLOYEE_SPACE - 1, i)
                    .borderStyle(BorderSide.BOTTOM, BorderStyle.THIN)
                    .set();
            i++;
        }
    }

    private List<String> calculateHours(LocalDate now, Map<LocalDate, DoctorLoad> dayWorkDoctors, int start, int end) {
        return IntStream.range(start, end + 1).mapToObj(now::withDayOfMonth).map(dayWorkDoctors::get).filter(Objects::nonNull)
                .map(v ->
                        Optional.ofNullable(v.getTakenExtraHours()).orElse(0d) + Optional.ofNullable(v.getTakenHours()).orElse(0d)
                ).reduce(Double::sum).map(this::time).map(this::format).map(List::of).orElse(List.of(this.format(Duration.ZERO)));
    }


    private String format(Duration duration) {
        return String.format("%02d:%02d", duration.toHours(), duration.toMinutesPart());
    }

    private Duration time(Double hoursDoubleObj) {
        final double hoursDouble = Optional.ofNullable(hoursDoubleObj).orElse(0d);
        long hours = (long) hoursDouble;
        long minutes = (long) ((hoursDouble - hours) * 60);
        long seconds = (long) (((hoursDouble - hours) * 60 - minutes) * 60);

        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    private void regularStyle(StyleSetter style) {
        style
                .horizontalAlignment("center")
                .verticalAlignment("center")
                .wrapText(true)
                .set();
    }
}

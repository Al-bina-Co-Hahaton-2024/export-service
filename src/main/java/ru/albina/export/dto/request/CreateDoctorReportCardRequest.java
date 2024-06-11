package ru.albina.export.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDoctorReportCardRequest {

    private LocalDate date;
}

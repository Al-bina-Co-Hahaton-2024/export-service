package ru.albina.export.dto.medical;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class Doctor {

    private UUID id;

    private Double hours;

    private Long serviceNumber;

    private Double rate;

    private Modality modality;

    private Set<Modality> optionalModality;

    private List<AbsenceSchedule> absenceSchedules;

    private LocalTime startWorkDay;
}

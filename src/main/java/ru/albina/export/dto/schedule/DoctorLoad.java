package ru.albina.export.dto.schedule;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DoctorLoad {

    private UUID doctorId;

    private Double manualExtraHours;

    private boolean forceSchedule;

    private Double takenHours;

    private Double takenExtraHours;

}

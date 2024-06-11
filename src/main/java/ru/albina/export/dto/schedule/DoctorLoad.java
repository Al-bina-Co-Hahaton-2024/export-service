package ru.albina.export.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorLoad {

    private UUID doctorId;

    private Double manualExtraHours;

    private boolean forceSchedule;

    private Double takenHours;

    private Double takenExtraHours;

}

package ru.albina.export.service.forecast;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.albina.export.client.ReferenceClient;
import ru.albina.export.dto.reference.GetOrGenerateYearlyWorkloadRequest;
import ru.albina.export.dto.reference.WeekNumberResult;
import ru.albina.export.dto.reference.Workload;
import ru.albina.export.mapper.ModalityMapper;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ForecastReportGenerator {

    private final ReferenceClient referenceClient;

    public List<WorkloadWeek> generate(int year) {
        final var weeks = this.referenceClient.getWeeks(List.of(LocalDate.of(year, 12, 31), LocalDate.of(year, 12, 31 - 7)))
                .stream().map(WeekNumberResult::getWeekNumber)
                .max(Integer::compareTo)
                .orElse(50);

        final var flatWorkload = this.referenceClient.getWorkload(
                List.of(
                        GetOrGenerateYearlyWorkloadRequest.builder()
                                .year(year)
                                .weeks(IntStream.range(1, weeks + 1).boxed().collect(Collectors.toSet()))
                                .build()
                )
        );

        final var map = new HashMap<String, WorkloadWeek>();

        for (Workload workload : flatWorkload) {
            WorkloadWeek workloadWeek;
            final var key = workload.getYear() + "" + workload.getWeek();

            if (map.containsKey(workload.getYear() + "" + workload.getWeek())) {
                workloadWeek = map.get(key);
            } else {
                workloadWeek = WorkloadWeek.builder()
                        .year(workload.getYear())
                        .week(workload.getWeek())
                        .build();
                map.put(key, workloadWeek);
            }

            workloadWeek.getModalities().put(
                    ModalityMapper.to(
                            workload.getModality(),
                            workload.getTypeModality()
                    ),
                    Optional.ofNullable(workload.getGeneratedValue()).orElse(-1L)
            );
        }

        return map.values().stream().sorted().toList();
    }
}

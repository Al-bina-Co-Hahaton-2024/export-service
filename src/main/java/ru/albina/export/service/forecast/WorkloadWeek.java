package ru.albina.export.service.forecast;

import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Builder
public class WorkloadWeek implements Comparable<WorkloadWeek> {

    private final Integer year;

    private final Integer week;

    private final Map<String, Long> modalities = new HashMap<>();


    @Override
    public int compareTo(WorkloadWeek o) {
        final var year = Integer.compare(this.year, o.year);
        if (year != 0) {
            return year;
        }

        return Integer.compare(this.week, o.week);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkloadWeek that = (WorkloadWeek) o;
        return Objects.equals(year, that.year) && Objects.equals(week, that.week);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, week);
    }
}

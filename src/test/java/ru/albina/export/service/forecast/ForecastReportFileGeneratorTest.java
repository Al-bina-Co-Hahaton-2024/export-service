package ru.albina.export.service.forecast;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.albina.export.component.FileComponent;

import java.util.stream.IntStream;

class ForecastReportFileGeneratorTest {

    private ForecastReportFileGenerator forecastReportFileGenerator;

    @BeforeEach
    void before() {
        this.forecastReportFileGenerator = new ForecastReportFileGenerator(new FileComponent());
    }

    @Test
    void should() {
        final var file = this.forecastReportFileGenerator.generateFile(
                IntStream.range(1, 9999).mapToObj(v ->
                        WorkloadWeek.builder()
                                .week(v)
                                .year(1211)
                                .build()
                ).toList()
        );
       // file.renameTo(new File("G:\\Projects\\hack\\export-service\\test.xlsx"));
    }

}
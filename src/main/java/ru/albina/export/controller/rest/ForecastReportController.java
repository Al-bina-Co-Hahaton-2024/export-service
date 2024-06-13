package ru.albina.export.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.albina.backlib.configuration.WebConstants;
import ru.albina.backlib.configuration.auto.OpenApiConfiguration;
import ru.albina.export.dto.request.CreateForecastReportRequest;
import ru.albina.export.dto.response.ExportDto;
import ru.albina.export.service.forecast.ForecastReportExportService;

@Slf4j
@RestController
@RequestMapping(WebConstants.FULL_WEB + "/forecast-reports")
@RequiredArgsConstructor
public class ForecastReportController {

    private final ForecastReportExportService forecastReportExportService;

    @Operation(
            summary = "Выгрузить прогноз за год",
            security = @SecurityRequirement(name = OpenApiConfiguration.JWT),
            responses = {
                    @ApiResponse(
                            description = "ОК",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = ExportDto.class))
                    )
            }
    )
    //TODO @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ExportDto createDoctorReportCard(
            @RequestBody CreateForecastReportRequest createDoctorReportCardRequest
    ) {
        return this.forecastReportExportService.generate(
                createDoctorReportCardRequest.getYear()
        );
    }
}

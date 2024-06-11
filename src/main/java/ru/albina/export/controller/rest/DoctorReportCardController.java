package ru.albina.export.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.albina.export.dto.request.CreateDoctorReportCardRequest;
import ru.albina.export.dto.response.ExportDto;
import ru.albina.export.service.DoctorReportCardExportService;

@Slf4j
@RestController
@RequestMapping(WebConstants.FULL_WEB + "/doctor-report-card")
@RequiredArgsConstructor
public class DoctorReportCardController {

    private final DoctorReportCardExportService doctorReportCardExportService;

    @Operation(
            summary = "Создать табель",
            security = @SecurityRequirement(name = OpenApiConfiguration.JWT),
            responses = {
                    @ApiResponse(
                            description = "ОК",
                            responseCode = "200"
                    )
            }
    )
    //TODO @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ExportDto createDoctorReportCard(
            @RequestBody CreateDoctorReportCardRequest createDoctorReportCardRequest
    ) {
        return this.doctorReportCardExportService.generate(
                createDoctorReportCardRequest.getDate()
        );
    }
}

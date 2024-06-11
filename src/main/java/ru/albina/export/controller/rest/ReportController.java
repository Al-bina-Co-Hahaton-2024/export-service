package ru.albina.export.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.albina.backlib.configuration.WebConstants;
import ru.albina.backlib.configuration.auto.OpenApiConfiguration;
import ru.albina.export.dto.response.ExportDto;
import ru.albina.export.service.ExportDtoService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(WebConstants.FULL_WEB + "/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ExportDtoService exportDtoService;

    @Operation(
            summary = "Получить статус отчета по ID",
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
    @GetMapping("/{id}")
    public ExportDto createDoctorReportCard(
            @PathVariable("id") UUID id
    ) {
        return this.exportDtoService.getById(id);
    }
}

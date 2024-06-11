package ru.albina.export.dto.response;

import lombok.Builder;
import ru.albina.export.domain.ExportStatus;
import ru.albina.export.domain.ExportType;

import java.util.UUID;

@Builder
public record ExportDto(
        UUID id,
        ExportType type,
        ExportStatus status,
        String link
) {
}

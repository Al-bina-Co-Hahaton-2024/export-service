package ru.albina.export.mapper;

import org.mapstruct.Mapper;
import ru.albina.export.configuration.MapperConfiguration;
import ru.albina.export.domain.ExportEntity;
import ru.albina.export.dto.response.ExportDto;

@Mapper(config = MapperConfiguration.class)
public interface ExportMapper {

    ExportDto to(ExportEntity exportEntity);
}

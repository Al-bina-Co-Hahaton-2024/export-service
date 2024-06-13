package ru.albina.export.mapper;

import org.mapstruct.Mapper;
import ru.albina.export.configuration.MapperConfiguration;
import ru.albina.export.dto.medical.Modality;
import ru.albina.export.dto.reference.TypeModality;

@Mapper(config = MapperConfiguration.class)
public interface ModalityMapper {

    static String to(Modality modality, TypeModality typeModality) {
        if (typeModality == null || typeModality == TypeModality.DEFAULT) {
            return modality.toString();
        }
        return modality + "_" + typeModality;
    }
}

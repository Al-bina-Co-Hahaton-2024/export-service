package ru.albina.export.mapper;

import org.mapstruct.Mapper;
import ru.albina.export.configuration.MapperConfiguration;
import ru.albina.export.dto.medical.Modality;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper(config = MapperConfiguration.class)
public interface MedicalMapper {


    default String modality(Collection<Modality> modalities) {
        return modalities.stream().map(this::modality).collect(Collectors.joining(", "));
    }


    default String modality(Modality modality) {
        return switch (modality) {
            case KT -> "КТ";
            case MRT -> "МРТ";
            case RG -> "РГ";
            case FLG -> "ФЛМ";
            case MMG -> "ММГ";
            case DENSITOMETER -> "Денситометрия";
        };
    }
}

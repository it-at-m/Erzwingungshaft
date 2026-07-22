package de.muenchen.eh.domain.identifier;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface PscdDataMapper {

    PscdDataMapper INSTANCE = Mappers.getMapper(PscdDataMapper.class);

    PscdDataExport toPscdExport(PscdDataImport pscdDataImport);
}

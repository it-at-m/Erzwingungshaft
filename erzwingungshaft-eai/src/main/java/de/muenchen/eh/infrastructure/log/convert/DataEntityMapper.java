package de.muenchen.eh.infrastructure.log.convert;

import de.muenchen.eh.domain.claim.ImportClaimData;
import de.muenchen.eh.infrastructure.db.entity.ClaimData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DataEntityMapper {

    DataEntityMapper INSTANCE = Mappers.getMapper(DataEntityMapper.class);

    ClaimData toClaimDataEntity(ImportClaimData importClaimData);
}



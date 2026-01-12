package de.muenchen.eh.log.convert;

import de.muenchen.eh.claim.ImportClaimData;
import de.muenchen.eh.db.entity.ClaimData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DataEntityMapper {

    DataEntityMapper INSTANCE = Mappers.getMapper(DataEntityMapper.class);

    ClaimData toClaimDataEntity(ImportClaimData importClaimData);
}

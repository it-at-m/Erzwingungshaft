package de.muenchen.eh.log.convert;

import de.muenchen.eh.kvue.claim.ClaimData;
import de.muenchen.eh.log.db.entity.ClaimDataEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DataEntityMapper {

    DataEntityMapper INSTANCE = Mappers.getMapper(DataEntityMapper.class);

    ClaimDataEntity toClaimDataEntity(ClaimData ehClaimData);
}

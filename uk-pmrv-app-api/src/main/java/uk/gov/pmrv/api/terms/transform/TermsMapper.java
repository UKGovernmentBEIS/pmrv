package uk.gov.pmrv.api.terms.transform;

import org.mapstruct.Mapper;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.terms.domain.Terms;
import uk.gov.pmrv.api.terms.domain.dto.TermsDTO;

/**
 * The Terms Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface TermsMapper {

    TermsDTO transformToTermsDTO(Terms terms);

}

package uk.gov.pmrv.api.workflow.payment.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.domain.dto.BankAccountDetailsDTO;
import uk.gov.pmrv.api.workflow.payment.repository.BankAccountDetailsRepository;
import uk.gov.pmrv.api.workflow.payment.transform.BankAccountDetailsMapper;

@Service
@RequiredArgsConstructor
public class BankAccountDetailsService {

    private final BankAccountDetailsRepository bankAccountDetailsRepository;
    private static final BankAccountDetailsMapper bankAccountDetailsMapper = Mappers.getMapper(BankAccountDetailsMapper.class);

    public BankAccountDetailsDTO getBankAccountDetailsByCa(CompetentAuthority competentAuthority) {
        return bankAccountDetailsRepository.findByCompetentAuthority(competentAuthority)
            .map(bankAccountDetailsMapper::toBankAccountDetailsDTO)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}

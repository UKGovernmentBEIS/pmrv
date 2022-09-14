package uk.gov.pmrv.api.permit.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.repository.PermitRepository;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;

@Service
@RequiredArgsConstructor
public class PermitService {

    private final PermitRepository permitRepo;
    private final PermitGrantedValidatorService permitGrantedValidatorService;
    private final PermitIdentifierGenerator generator;
    private final AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;

    @Transactional
    public String submitPermit(PermitContainer permitContainer, Long accountId) {
        //validate
        permitGrantedValidatorService.validatePermit(permitContainer);

        String permitId = generator.generate(accountId);

        //submit
        PermitEntity permitEntity = PermitEntity.builder()
            .id(permitId)
            .permitContainer(permitContainer)
            .accountId(accountId)
            .build();
        PermitEntity submittedPermitEntity = permitRepo.save(permitEntity);

        accountSearchAdditionalKeywordService.storeKeywordForAccount(permitId, accountId);

        return submittedPermitEntity.getId();
    }

}

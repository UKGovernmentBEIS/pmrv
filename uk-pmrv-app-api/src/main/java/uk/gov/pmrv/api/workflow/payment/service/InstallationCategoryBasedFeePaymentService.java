package uk.gov.pmrv.api.workflow.payment.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsDTO;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.account.transform.InstallationCategoryMapper;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.payment.repository.PaymentFeeMethodRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.math.BigDecimal;

@Service
public class InstallationCategoryBasedFeePaymentService extends PaymentService {

    private final AccountQueryService accountQueryService;

    public InstallationCategoryBasedFeePaymentService(PaymentFeeMethodRepository paymentFeeMethodRepository,
                                                      AccountQueryService accountQueryService) {
        super(paymentFeeMethodRepository);
        this.accountQueryService = accountQueryService;
    }

    @Override
    public FeeMethodType getFeeMethodType() {
        return FeeMethodType.INSTALLATION_CATEGORY_BASED;
    }

    @Override
    protected FeeType resolveFeeType(Request request) {
        EmitterType emitterType;
        InstallationCategory installationCategory;
        if(request.getType() == RequestType.PERMIT_ISSUANCE) {
            PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();
            emitterType = requestPayload.getPermitType() == PermitType.HSE ? EmitterType.HSE : EmitterType.GHGE;
            BigDecimal estimatedAnnualEmissions = requestPayload.getPermit().getEstimatedAnnualEmissions().getQuantity();
            installationCategory = InstallationCategoryMapper.getInstallationCategory(emitterType, estimatedAnnualEmissions);
        } else {
            AccountDetailsDTO accountDetails = accountQueryService.getAccountDetailsDTOById(request.getAccountId());
            emitterType = accountDetails.getEmitterType();
            installationCategory = accountDetails.getInstallationCategory();
        }

        return resolveFeeType(emitterType, installationCategory);
    }

    private FeeType resolveFeeType(EmitterType emitterType, InstallationCategory installationCategory) {
        if(emitterType == null || installationCategory == null) {
            return null;
        }

        if(emitterType == EmitterType.HSE) {
            return FeeType.HSE;
        } else {
            switch(installationCategory) {
                case A_LOW_EMITTER:
                case A:
                    return  FeeType.CAT_A;
                case B:
                    return FeeType.CAT_B;
                case C:
                    return FeeType.CAT_C;
                default:
                    return null;
            }
        }
    }
}

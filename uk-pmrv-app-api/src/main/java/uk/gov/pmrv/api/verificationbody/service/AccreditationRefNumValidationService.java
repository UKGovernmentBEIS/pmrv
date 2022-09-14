package uk.gov.pmrv.api.verificationbody.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

@Service
@RequiredArgsConstructor
public class AccreditationRefNumValidationService {

    private final VerificationBodyRepository verificationBodyRepository;

    public void validate(String accreditationReferenceNumber, Long verificationBodyId) {
        List<String> existingReferenceNumbers = getExistingAccreditationReferenceNumbersInOtherVerificationBodies(verificationBodyId);
        validate(accreditationReferenceNumber, existingReferenceNumbers);
    }

    public void validate(String accreditationReferenceNumber) {
        List<String> existingAccreditationReferenceNumbers = getExistingAccreditationReferenceNumbers();
        validate(accreditationReferenceNumber, existingAccreditationReferenceNumbers);
    }

    private List<String> getExistingAccreditationReferenceNumbers() {
        List<VerificationBody> verificationBodies = verificationBodyRepository.findAll();
        return getAccreditationReferenceNumbers(verificationBodies);
    }

    private List<String> getExistingAccreditationReferenceNumbersInOtherVerificationBodies(Long verificationBodyId) {
        List<VerificationBody> verificationBodies = verificationBodyRepository.findByIdNot(verificationBodyId);
        return getAccreditationReferenceNumbers(verificationBodies);
    }

    private List<String> getAccreditationReferenceNumbers(List<VerificationBody> verificationBodies) {
        return verificationBodies.stream()
            .map(VerificationBody::getAccreditationReferenceNumber)
            .collect(Collectors.toList());
    }

    private void validate(String accreditationReferenceNumber, List<String> existingAccreditationReferenceNumbers) {
        if(existingAccreditationReferenceNumbers.contains(accreditationReferenceNumber)) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_CONTAINS_NON_UNIQUE_REF_NUM, accreditationReferenceNumber);
        }
    }
}

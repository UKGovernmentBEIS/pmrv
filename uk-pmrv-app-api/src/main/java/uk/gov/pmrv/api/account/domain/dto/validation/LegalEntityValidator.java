package uk.gov.pmrv.api.account.domain.dto.validation;

import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static uk.gov.pmrv.api.common.domain.dto.validation.ValidationUtils.addConstraintViolation;

/**
 * The legal entity validator.
 */
public class LegalEntityValidator implements ConstraintValidator<LegalEntity, LegalEntityDTO> {

    private static final int REF_NUMBER_MAX_SIZE = 15;
    
    /** Max length for no companies house reference number */
    private static final int NO_REF_NUMBER_MAX_SIZE = 500;

    /** Max length for legal entity name */
    private static final int MAX_SIZE_LEGAL_ENTITY = 255;

    /** {@inheritDoc} */
    @Override
    public boolean isValid(LegalEntityDTO legalEntity, ConstraintValidatorContext context) {
        if (legalEntity == null) {
            return true;
        }
        
        if(legalEntity.getId() != null) {
            return true;
        }
        
        boolean isValid;
        context.disableDefaultConstraintViolation();

        isValid = validateReferenceNumber(context, legalEntity.getReferenceNumber(), legalEntity.getNoReferenceNumberReason());

        String legalEntityName = legalEntity.getName();

        if(ObjectUtils.isEmpty(legalEntityName)) {
            addConstraintViolation(context, "{legalEntity.name.notEmpty}", "name");
            isValid = false;
        } else {
            if(legalEntityName.length() > MAX_SIZE_LEGAL_ENTITY) {
                addConstraintViolation(context, "{legalEntity.name.typeMismatch}", "name");
                isValid = false;
            }
        }

        if(ObjectUtils.isEmpty(legalEntity.getType())) {
            addConstraintViolation(context, "{legalEntity.type.notEmpty}", "type");
            isValid = false;
        }

        if(ObjectUtils.isEmpty(legalEntity.getAddress())) {
            addConstraintViolation(context, "{locationOnShoreDTO.address.notEmpty}", "address");
            isValid = false;
        }

        return isValid;

    }

    private boolean validateReferenceNumber(ConstraintValidatorContext context, String referenceNumber,
            String noReferenceNumberReason) {
        if (ObjectUtils.isEmpty(referenceNumber)) {
            if (ObjectUtils.isEmpty(noReferenceNumberReason)) {
                addConstraintViolation(context, "{legalEntity.referenceNumber.notEmpty}", "referenceNumber");
                return false;
            } else {
                if (noReferenceNumberReason.length() > NO_REF_NUMBER_MAX_SIZE) {
                    addConstraintViolation(context, "{legalEntity.noReferenceNumberReason.typeMismatch}",
                            "noReferenceNumberReason");
                    return false;
                }
            }
        } else {
            if (referenceNumber.length() > REF_NUMBER_MAX_SIZE) {
                addConstraintViolation(context, "{legalEntity.referenceNumber.typeMismatch}", "referenceNumber");
                return false;
            }
        }

        return true;
    }

}

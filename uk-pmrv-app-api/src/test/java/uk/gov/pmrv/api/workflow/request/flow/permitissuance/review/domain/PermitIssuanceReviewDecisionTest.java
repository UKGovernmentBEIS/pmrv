package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;

public class PermitIssuanceReviewDecisionTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void permitIssuanceAcceptedValidModel() {
        PermitIssuanceReviewDecision permitIssuanceReviewDecision = PermitIssuanceReviewDecision.builder()
            .notes("Notes")
            .type(ReviewDecisionType.ACCEPTED)
            .build();

        Set<ConstraintViolation<PermitIssuanceReviewDecision>> violations = validator.validate(permitIssuanceReviewDecision);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void permitIssuanceRejectedValidModel() {
        PermitIssuanceReviewDecision permitIssuanceReviewDecision = PermitIssuanceReviewDecision.builder()
            .notes("Notes")
            .type(ReviewDecisionType.REJECTED)
            .build();

        Set<ConstraintViolation<PermitIssuanceReviewDecision>> violations = validator.validate(permitIssuanceReviewDecision);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void permitIssuanceOperatorAmendsValidModel() {
        PermitIssuanceReviewDecision permitIssuanceReviewDecision = PermitIssuanceReviewDecision.builder()
            .notes("Notes")
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .requiredChange(new PermitReviewDecisionRequiredChange("change", Set.of(UUID.randomUUID())))
            .build();

        Set<ConstraintViolation<PermitIssuanceReviewDecision>> violations = validator.validate(permitIssuanceReviewDecision);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void permitIssuanceOperatorAmendsInvalidModelWithNoChanges() {
        PermitIssuanceReviewDecision permitIssuanceReviewDecision = PermitIssuanceReviewDecision.builder()
            .notes("Notes")
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .build();

        Set<ConstraintViolation<PermitIssuanceReviewDecision>> violations = validator.validate(permitIssuanceReviewDecision);

        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void permitIssuanceOperatorAmendsInvalidModelWithBlankChanges() {
        PermitIssuanceReviewDecision permitIssuanceReviewDecision = PermitIssuanceReviewDecision.builder()
            .notes("Notes")
            .requiredChange(new PermitReviewDecisionRequiredChange("", Collections.emptySet()))
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .build();

        Set<ConstraintViolation<PermitIssuanceReviewDecision>> violations = validator.validate(permitIssuanceReviewDecision);

        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void permitIssuanceAcceptedInvalidModelWithRequiredChanges() {
        PermitIssuanceReviewDecision permitIssuanceReviewDecision = PermitIssuanceReviewDecision.builder()
            .notes("Notes")
            .requiredChange(new PermitReviewDecisionRequiredChange("", Collections.emptySet()))
            .type(ReviewDecisionType.ACCEPTED)
            .build();

        Set<ConstraintViolation<PermitIssuanceReviewDecision>> violations = validator.validate(permitIssuanceReviewDecision);

        assertThat(violations.size()).isEqualTo(1);
    }
}

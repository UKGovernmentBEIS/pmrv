package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Collections;
import java.util.List;
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

public class PermitVariationReviewDecisionTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void validOperatorAmendsNeededModel() {
        PermitVariationReviewDecision permitVariationReviewDecision = PermitVariationReviewDecision.builder()
            .notes("Foo")
            .requiredChanges(List.of(PermitReviewDecisionRequiredChange.builder().changesRequired("change1").files(Set.of(UUID.randomUUID())).build()))
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .build();

        Set<ConstraintViolation<PermitVariationReviewDecision>> violations = validator.validate(permitVariationReviewDecision);

        assertThat(violations.size()).isEqualTo(0);
    }

    @Test
    public void invalidOperatorAmendsNeededNoChangesRequired() {
        PermitVariationReviewDecision permitVariationReviewDecision = PermitVariationReviewDecision.builder()
            .notes("Foo")
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .build();

        Set<ConstraintViolation<PermitVariationReviewDecision>> violations = validator.validate(permitVariationReviewDecision);

        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void invalidOperatorAmendsNeededEmptyRequiredChanges() {
        PermitVariationReviewDecision permitVariationReviewDecision = PermitVariationReviewDecision.builder()
            .notes("Foo")
            .requiredChanges(List.of(new PermitReviewDecisionRequiredChange("change1", Collections.emptySet()),
                new PermitReviewDecisionRequiredChange("", Collections.emptySet())))
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .build();

        Set<ConstraintViolation<PermitVariationReviewDecision>> violations = validator.validate(permitVariationReviewDecision);

        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void invalidAcceptedWithChangesRequired() {
        PermitVariationReviewDecision permitVariationReviewDecision = PermitVariationReviewDecision.builder()
            .notes("Foo")
            .requiredChanges(List.of(PermitReviewDecisionRequiredChange.builder().changesRequired("change1").files(Set.of(UUID.randomUUID())).build()))
            .type(ReviewDecisionType.ACCEPTED)
            .build();

        Set<ConstraintViolation<PermitVariationReviewDecision>> violations = validator.validate(permitVariationReviewDecision);

        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void invalidRejectedWithChangesRequired() {
        PermitVariationReviewDecision permitVariationReviewDecision = PermitVariationReviewDecision.builder()
            .notes("Foo")
            .requiredChanges(List.of(PermitReviewDecisionRequiredChange.builder().changesRequired("change1").files(Set.of(UUID.randomUUID())).build()))
            .type(ReviewDecisionType.REJECTED)
            .build();

        Set<ConstraintViolation<PermitVariationReviewDecision>> violations = validator.validate(permitVariationReviewDecision);

        assertThat(violations.size()).isEqualTo(1);
    }

}

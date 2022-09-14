package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PermitSurrenderReviewDeterminationGrantTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void validModel() {
        PermitSurrenderReviewDeterminationGrant grant = PermitSurrenderReviewDeterminationGrant.builder()
            .stopDate(LocalDate.now())
            .reason("reason")
            .type(PermitSurrenderReviewDeterminationType.GRANTED)
            .noticeDate(LocalDate.now().plusDays(30))
            .reportDate(LocalDate.now().plusDays(1))
            .allowancesSurrenderDate(LocalDate.now().plusDays(1))
            .allowancesSurrenderRequired(Boolean.TRUE)
            .reportRequired(Boolean.TRUE)
            .build();

        Set<ConstraintViolation<PermitSurrenderReviewDeterminationGrant>> violations = validator.validate(grant);

        assertThat(violations.size()).isEqualTo(0);
    }

}

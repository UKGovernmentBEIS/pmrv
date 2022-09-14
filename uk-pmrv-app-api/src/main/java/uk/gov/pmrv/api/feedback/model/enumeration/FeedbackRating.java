package uk.gov.pmrv.api.feedback.model.enumeration;

public enum FeedbackRating {
    VERY_SATISFIED("Very satisfied"),
    SATISFIED("Satisfied"),
    NEITHER_SATISFIED_NOR_DISSATISFIED("Neither satisfied or dissatisfied"),
    DISSATISFIED("Dissatisfied"),
    VERY_DISSATISFIED("Very dissatisfied"),
    NOT_APPLICABLE_NOT_USED_YET("Not applicable or not used yet");

    private final String description;

    FeedbackRating(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

package uk.gov.pmrv.api.permit.domain;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class PermitViolation {

    private String sectionName;
    private String message;
    private Object[] data;
    
    public PermitViolation(String sectionName, PermitViolationMessage permitViolationMessage) {
        this(sectionName, permitViolationMessage, List.of());
    }
    
    public PermitViolation(PermitViolationMessage permitViolationMessage, Object... data) {
        this(null, permitViolationMessage, data);
    }

    public PermitViolation(String sectionName, PermitViolationMessage permitViolationMessage, Object... data) {
        this.sectionName = sectionName;
        this.message = permitViolationMessage.getMessage();
        this.data = data;
    }

    @Getter
    public enum PermitViolationMessage {
        ACCOUNTING_EMISSIONS_NOT_FOUND("Missing justification for not transferred CO2 chemically bound in precipitated calcium carbonate"),
        ATTACHMENT_NOT_FOUND("Attachment not found"),
        INVALID_SOURCE_STREAM("Referenced Source stream is not defined in Permit"),
        INVALID_EMISSION_SOURCE("Referenced Emission source is not defined in Permit"),
        INVALID_EMISSION_POINT("Referenced Emission point is not defined in Permit"),
        INVALID_REGULATED_ACTIVITY("Referenced Regulated activity is not defined in Permit"),
        INVALID_MEASUREMENT_DEVICE_OR_METHOD("Referenced measurement device or method is not defined in the permit"),
        EMISSION_SUMMARY_INVALID_EXCLUDED_ACTIVITY("An excluded activity cannot reference a Regulated activity"),
        EMISSION_SUMMARIES_SOURCE_STREAM_SHOULD_EXIST("Some Source streams are not referenced by any emission summary"),
        EMISSION_SUMMARIES_EMISSION_SOURCE_SHOULD_EXIST("Some Emission sources are not referenced by any emission summary"),
        EMISSION_SUMMARIES_EMISSION_POINT_SHOULD_EXIST("Some Emission points are not referenced by any emission summary"),
        EMISSION_SUMMARIES_REGULATED_ACTIVITY_SHOULD_EXIST("Some Regulated activities are not referenced by any emission summary"),
        ANNUAL_EMISSIONS_TARGET_NOT_FOUND("Enter the annual emissions target"),
        ANNUAL_EMISSIONS_TARGET_INVALID_YEAR("An invalid year has been defined for annual emissions target"),
        ANNUAL_EMISSIONS_TARGET_INVALID_PRECISION("Annual emissions target precision should be 1 decimal"),
        INVALID_ANNUAL_EMISSIONS_TARGET("Annual emissions target should not be defined"),
        INVALID_ACTIVATION_DATE("Activation date is not valid")
        ;

        private final String message;

        PermitViolationMessage(String message) {
            this.message = message;
        }
    }
}

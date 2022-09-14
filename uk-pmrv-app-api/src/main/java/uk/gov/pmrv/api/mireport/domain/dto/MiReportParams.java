package uk.gov.pmrv.api.mireport.domain.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "reportType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EmptyMiReportParams.class, name = "LIST_OF_ACCOUNTS_USERS_CONTACTS"),
    @JsonSubTypes.Type(value = ExecutedRequestActionsMiReportParams.class, name = "COMPLETED_WORK")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class MiReportParams {

    private MiReportType reportType;
}

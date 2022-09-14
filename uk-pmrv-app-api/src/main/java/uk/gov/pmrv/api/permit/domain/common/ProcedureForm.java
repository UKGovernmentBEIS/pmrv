package uk.gov.pmrv.api.permit.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureForm {

    @NotBlank
    @Size(max = 10000)
    private String procedureDescription;

    @NotBlank
    @Size(max = 1000)
    private String procedureDocumentName;

    @NotBlank
    @Size(max = 500)
    private String procedureReference;

    @Size(max = 500)
    private String diagramReference;

    @NotBlank
    @Size(max = 1000)
    private String responsibleDepartmentOrRole;

    @NotBlank
    @Size(max = 2000)
    private String locationOfRecords;

    @Size(max = 500)
    private String itSystemUsed;

    @Size(max = 2000)
    private String appliedStandards;
}

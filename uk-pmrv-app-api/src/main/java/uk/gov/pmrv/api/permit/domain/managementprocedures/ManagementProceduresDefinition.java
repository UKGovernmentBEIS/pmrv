package uk.gov.pmrv.api.permit.domain.managementprocedures;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.permit.domain.PermitSection;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ManagementProceduresDefinition extends ProcedureForm implements PermitSection {
}

package uk.gov.pmrv.api.permit.domain.measurementdevices;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;
import uk.gov.pmrv.api.permit.domain.PermitIdSection;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type eq 'OTHER') == (#otherTypeName != null)}", message = "permit.measurementDeviceOrMethod.otherTypeName")
@SpELExpression(expression = "{#uncertaintySpecified == (#specifiedUncertaintyPercentage != null)}", message = "permit.measurementDeviceOrMethod.uncertaintySpecified")
public class MeasurementDeviceOrMethod extends PermitIdSection {

    //TODO revisit validation
    @Size(max = 10000)
    @NotBlank
    private String reference;

    @NotNull
    private MeasurementDeviceType type;

    //TODO revisit validation
    @Size(max = 10000)
    private String otherTypeName;

    //TODO revisit validation
    @Size(max = 10000)
    @NotBlank
    private String measurementRange;

    //TODO revisit validation
    @Size(max = 10000)
    @NotBlank
    private String meteringRangeUnits;

    private boolean uncertaintySpecified;

    @DecimalMin(value = "0")
    @Digits(integer = 2, fraction = 3)
    private BigDecimal specifiedUncertaintyPercentage;

    //TODO revisit validation
    @Size(max = 10000)
    @NotBlank
    private String location;

}

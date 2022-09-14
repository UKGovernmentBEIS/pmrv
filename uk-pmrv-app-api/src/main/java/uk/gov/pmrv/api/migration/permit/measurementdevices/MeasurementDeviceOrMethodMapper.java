package uk.gov.pmrv.api.migration.permit.measurementdevices;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {UUID.class, MeasurementDeviceTypeMapper.class})
public interface MeasurementDeviceOrMethodMapper {

    @Mapping(target = "id", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "type", expression = "java(MeasurementDeviceTypeMapper.getMeasurementDeviceType(etsMeteringMeasurementDevice.getType()))")
    @Mapping(target = "measurementRange", source = "range")
    @Mapping(target = "meteringRangeUnits", source = "rangeUnits")
    MeasurementDeviceOrMethod toMeasurementDeviceOrMethod(EtsMeteringMeasurementDevice etsMeteringMeasurementDevice);

    List<MeasurementDeviceOrMethod> toMeasurementDeviceOrMethodList(
        List<EtsMeteringMeasurementDevice> etsMeteringMeasurementDeviceList);

    @AfterMapping
    default void setOtherTypeNameAndUncertainty(@MappingTarget MeasurementDeviceOrMethod measurementDeviceOrMethod, EtsMeteringMeasurementDevice etsMeteringMeasurementDevice) {
        if(measurementDeviceOrMethod.getType().equals(MeasurementDeviceType.OTHER))
            measurementDeviceOrMethod.setOtherTypeName(etsMeteringMeasurementDevice.getType());

        String etsUncertainty = etsMeteringMeasurementDevice.getUncertainty();
        if(NumberUtils.isParsable(etsUncertainty)) {
            measurementDeviceOrMethod.setUncertaintySpecified(true);
            measurementDeviceOrMethod.setSpecifiedUncertaintyPercentage(new BigDecimal(etsUncertainty));
        }
    }
}

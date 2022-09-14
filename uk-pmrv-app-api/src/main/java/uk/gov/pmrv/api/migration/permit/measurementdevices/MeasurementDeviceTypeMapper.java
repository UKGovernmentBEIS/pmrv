package uk.gov.pmrv.api.migration.permit.measurementdevices;

import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.BALANCE;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.BELLOWS_METER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.BELT_WEIGHER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.CORIOLIS_METER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.ELECTRONIC_VOLUME_CONVERSION_INSTRUMENT;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.GAS_CHROMATOGRAPH;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.LEVEL_GAUGE;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.ORIFICE_METER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.OTHER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.OVALRAD_METER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.ROTARY_METER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.TANK_DIP;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.TURBINE_METER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.ULTRASONIC_METER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.VENTURI_METER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.VORTEX_METER;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.WEIGHBRIDGE;
import static uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType.WEIGHSCALE;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.UtilityClass;

import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;

@UtilityClass
public class MeasurementDeviceTypeMapper {

    static final Map<String, MeasurementDeviceType> measurementDeviceTypes = new HashMap<>();

    static {
        measurementDeviceTypes.put("Balance", BALANCE);
        measurementDeviceTypes.put("Bellows meter", BELLOWS_METER);
        measurementDeviceTypes.put("Belt weigher", BELT_WEIGHER);
        measurementDeviceTypes.put("Coriolis meter", CORIOLIS_METER);
        measurementDeviceTypes.put("Electronic volume conversion instrument (EVCI)", ELECTRONIC_VOLUME_CONVERSION_INSTRUMENT);
        measurementDeviceTypes.put("Gas chromatograph", GAS_CHROMATOGRAPH);
        measurementDeviceTypes.put("Level gauge", LEVEL_GAUGE);
        measurementDeviceTypes.put("Orifice meter", ORIFICE_METER);
        measurementDeviceTypes.put("Ovalrad meter", OVALRAD_METER);
        measurementDeviceTypes.put("Rotary meter", ROTARY_METER);
        measurementDeviceTypes.put("Tank dip", TANK_DIP);
        measurementDeviceTypes.put("Turbine meter", TURBINE_METER);
        measurementDeviceTypes.put("Ultrasonic meter", ULTRASONIC_METER);
        measurementDeviceTypes.put("Venturi meter", VENTURI_METER);
        measurementDeviceTypes.put("Vortex meter", VORTEX_METER);
        measurementDeviceTypes.put("Weighbridge", WEIGHBRIDGE);
        measurementDeviceTypes.put("Weighscale", WEIGHSCALE);
    }

    public MeasurementDeviceType getMeasurementDeviceType(String etsMeasurementDeviceType) {
        MeasurementDeviceType measurementDeviceType = measurementDeviceTypes.get(etsMeasurementDeviceType);
        return measurementDeviceType != null ? measurementDeviceType : OTHER;
    }
}

package uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.cellanodetypes;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.CellAndAnodeType;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@UtilityClass
public final class PFCApproachCellAndAnodeTypesMapper {
    public static List<CellAndAnodeType> constructPFCCellAndAnodeTypes(List<EtsCellAndAnodeType> etsCellAndAnodeTypes) {
        List<CellAndAnodeType> cellAndAnodeTypes = new ArrayList<>();
        for (EtsCellAndAnodeType etsCellAndAnodeType : etsCellAndAnodeTypes) {
            cellAndAnodeTypes.add(CellAndAnodeType.builder()
                    .cellType(etsCellAndAnodeType.getCellType())
                    .anodeType(etsCellAndAnodeType.getAnodeType())
                    .build());
        }
        return cellAndAnodeTypes;
    }
}

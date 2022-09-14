package uk.gov.pmrv.api.migration.installationaccount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class InstallationAccountHelper {

    public String constructQuery(String query, String ids) {
        StringBuilder queryBuilder = new StringBuilder(query);

        List<String> idList =
            !StringUtils.isBlank(ids) ? new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();
        if (!idList.isEmpty()) {
            queryBuilder.append(" and e.fldEmitterID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        return queryBuilder.toString();
    }

    public String constructErrorMessage(String id, String name, String displayId, String errorMessage, String data) {
        return "emitterId: " + id +
            " | emitterDisplayId: " + displayId +
            " | emitterName: " + name +
            " | Error: " + errorMessage +
            " | data: " + data;
    }

    public String constructErrorMessageWithCA(String id, String name, String displayId, String ca, String errorMessage,
                                              String data) {
        return "emitterId: " + id +
            " | emitterDisplayId: " + displayId +
            " | emitterName: " + name +
            " | CA: " + ca +
            " | Error: " + errorMessage +
            " | data: " + data;
    }

    public String constructSuccessMessage(String id, String name, String displayId, String ca) {
        return "emitterId: " + id +
            " | emitterDisplayId: " + displayId +
            " | emitterName: " + name +
            " | CA: " + ca;
    }
}

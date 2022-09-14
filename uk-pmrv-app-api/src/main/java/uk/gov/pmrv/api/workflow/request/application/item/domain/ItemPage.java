package uk.gov.pmrv.api.workflow.request.application.item.domain;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemPage {

    List<Item> items;

    Long totalItems;

}

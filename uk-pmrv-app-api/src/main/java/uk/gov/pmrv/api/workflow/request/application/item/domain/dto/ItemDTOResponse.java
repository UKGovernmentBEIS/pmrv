package uk.gov.pmrv.api.workflow.request.application.item.domain.dto;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDTOResponse {

    List<ItemDTO> items;

    Long totalItems;

    public static ItemDTOResponse emptyItemDTOResponse() {
        return ItemDTOResponse.builder().items(Collections.emptyList()).totalItems(0L).build();
    }

}

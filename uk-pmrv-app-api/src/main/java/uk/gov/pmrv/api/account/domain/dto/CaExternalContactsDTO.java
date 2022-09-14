package uk.gov.pmrv.api.account.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaExternalContactsDTO {

    private List<CaExternalContactDTO> caExternalContacts;

    @JsonProperty("isEditable")
    private boolean isEditable;
}

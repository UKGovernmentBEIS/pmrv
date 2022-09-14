package uk.gov.pmrv.api.workflow.request.flow.rfi.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RfiSubmittedRequestActionPayload extends RequestActionPayload {

    @NotNull
    @Valid
    private RfiSubmitPayload rfiSubmitPayload;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> rfiAttachments = new HashMap<>();
    
    @NotNull
    private FileInfoDTO officialDocument;
    
    @Override
    public Map<UUID, String> getAttachments() {
        return this.rfiAttachments;
    }
    
    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream
                .of(super.getFileDocuments(),
                        Map.of(UUID.fromString(officialDocument.getUuid()), officialDocument.getName()))
                .flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}

package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitRevocationApplicationWithdrawnRequestActionPayload extends RequestActionPayload {

    @NotBlank
    @Size(max = 10000)
    private String reason;

    @Builder.Default
    private Set<UUID> withdrawFiles = new HashSet<>();

    @Builder.Default
    private Map<UUID, String> revocationAttachments = new HashMap<>();

    @Valid
    @NotNull
    private DecisionNotification decisionNotification;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();
    
    @NotNull
    private FileInfoDTO withdrawnOfficialNotice;
    
    @Override
    public Map<UUID, String> getAttachments() {
		return Stream.of(
				super.getAttachments(), 
				getRevocationAttachments()).flatMap(m -> m.entrySet().stream())
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
    
    @Override
    public Map<UUID, String> getFileDocuments() {
		return Stream
				.of(super.getFileDocuments(), 
					Map.of(UUID.fromString(withdrawnOfficialNotice.getUuid()), withdrawnOfficialNotice.getName()))
				.flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}

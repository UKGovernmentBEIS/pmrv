package uk.gov.pmrv.api.user.core.domain.model.core;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureRequest {

    @NotEmpty
    private byte[] content;
    
    @NotBlank
    private String name;
    
    @NotNull
    @Positive
    private Long size;
    
    @NotBlank
    private String type;
}

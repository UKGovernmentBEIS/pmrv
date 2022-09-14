package gov.uk.pmrv.keycloak.user.api.model.jpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Signature {

    @EqualsAndHashCode.Include()
    @NotBlank
    @Column(name = "signature_uuid")
    private String signatureUuid;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="signature_content")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] signatureContent;

    @NotBlank
    @Column(name = "signature_name")
    private String signatureName;

    @NotNull
    @Positive
    @Column(name = "signature_size")
    private Long signatureSize;

    @NotBlank
    @Column(name = "signature_type")
    private String signatureType;

}

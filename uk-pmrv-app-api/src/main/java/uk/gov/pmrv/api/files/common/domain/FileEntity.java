package uk.gov.pmrv.api.files.common.domain;

import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_file_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "uuid", unique = true)
    @NotBlank
    private String uuid;

    @Column(name = "file_name")
    @NotBlank
    private String fileName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="file_content")
    @Type(type="org.hibernate.type.BinaryType")
    private byte[] fileContent;

    /**
     * The file size (in bytes).
     */
    @Column(name = "file_size")
    @Positive
    private long fileSize;

    @Column(name = "file_type")
    @NotBlank
    private String fileType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private FileStatus status;

    @Column(name = "created_by")
    @NotBlank
    private String createdBy;

    @Column(name = "last_updated_on")
    @LastModifiedDate
    @NotNull
    private LocalDateTime lastUpdatedOn;
}

package uk.gov.pmrv.api.notification.template.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification_document_template")
@SqlResultSetMapping(
    name = DocumentTemplate.DOCUMENT_TEMPLATE_INFO_DTO_RESULT_MAPPER,
    classes = {
        @ConstructorResult(
            targetClass = TemplateInfoDTO.class,
            columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "name"),
                @ColumnResult(name = "operatorType", type = String.class),
                @ColumnResult(name = "workflow"),
                @ColumnResult(name = "lastUpdatedDate", type= LocalDateTime.class)
            }
        )})
public class DocumentTemplate {

    public static final String DOCUMENT_TEMPLATE_INFO_DTO_RESULT_MAPPER = "DocumentTemplateInfoDTOResultMapper";

    @Id
    @SequenceGenerator(name = "document_template_id_generator", sequenceName = "notification_document_template_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_template_id_generator")
    private Long id;

    @EqualsAndHashCode.Include
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DocumentTemplateType type;

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    @NotNull
    private CompetentAuthority competentAuthority;
    
    @NotBlank
    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator_type")
    private TemplateOperatorType operatorType;

    @Column(name = "workflow")
    @NotBlank
    private String workflow;

    @Column(name = "last_updated_date")
    @LastModifiedDate
    @NotNull
    private LocalDateTime lastUpdatedDate;
    
    @NotNull
    @Column(name = "file_document_template_id", unique = true)
    private Long fileDocumentTemplateId;

    @Builder.Default
    @ManyToMany(mappedBy = "documentTemplates")
    @ToString.Exclude
    private Set<NotificationTemplate> notificationTemplates = new HashSet<>();
}

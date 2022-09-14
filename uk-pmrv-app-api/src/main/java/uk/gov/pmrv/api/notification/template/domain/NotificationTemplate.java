package uk.gov.pmrv.api.notification.template.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.notification.template.domain.converter.NotificationTemplateNameConverter;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.notification.template.domain.enumeration.TemplateOperatorType;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the notification_template database table.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification_template")
@NamedEntityGraph(
    name = "notification-templates-graph",
    attributeNodes = {
        @NamedAttributeNode("text"),
        @NamedAttributeNode("documentTemplates")
    })
@NamedQuery(
    name = NotificationTemplate.NAMED_QUERY_FIND_MANAGED_NOTIFICATION_TEMPLATE_BY_ID,
    query = "select template from NotificationTemplate template "
        + "where template.id = :id "
        + "and template.managed = true")
@SqlResultSetMapping(
    name = NotificationTemplate.NOTIFICATION_TEMPLATE_INFO_DTO_RESULT_MAPPER,
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
public class NotificationTemplate {

    public static final String NOTIFICATION_TEMPLATE_INFO_DTO_RESULT_MAPPER = "NotificationTemplateInfoDTOResultMapper";
    public static final String NAMED_QUERY_FIND_MANAGED_NOTIFICATION_TEMPLATE_BY_ID = "NotificationTemplate.findManagedNotificationTemplateById";

    @Id
    @SequenceGenerator(name = "notification_template_id_generator", sequenceName = "notification_template_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_template_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Convert(converter = NotificationTemplateNameConverter.class)
    @Column(name = "name")
    private NotificationTemplateName name;

    @NotNull
    @Column(name = "subject")
    private String subject;

    @NotNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "text")
    private String text;

    @EqualsAndHashCode.Include()
    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    private CompetentAuthority competentAuthority;

    @Column(name = "event_trigger")
    private String eventTrigger;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator_type")
    private TemplateOperatorType operatorType;

    @Column(name = "workflow")
    private String workflow;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;

    @Column(name = "is_managed", columnDefinition = "boolean default false")
    private boolean managed;

    @Column(name = "last_updated_date")
    @LastModifiedDate
    private LocalDateTime lastUpdatedDate;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "notification_template_document_template",
        joinColumns = @JoinColumn(name = "notification_template_id"),
        inverseJoinColumns = @JoinColumn(name = "document_template_id")
    )
    @ToString.Exclude
    private Set<DocumentTemplate> documentTemplates = new HashSet<>();
}

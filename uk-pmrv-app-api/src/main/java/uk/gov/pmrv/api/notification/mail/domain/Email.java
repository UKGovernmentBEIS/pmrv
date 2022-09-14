package uk.gov.pmrv.api.notification.mail.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object that holds the appropriate information in order to send an email.
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class Email {

    private String sendFrom;
    @Builder.Default
    private List<String> sendTo = new ArrayList<>();
    @Builder.Default
    private List<String> sendCc = new ArrayList<>();
    private String subject;
    private String text;
    
    /**
     * key: attachment name
     * value: the file content
     */
    @Builder.Default
    private Map<String, byte[]> attachments = new HashMap<>();
}

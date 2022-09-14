package uk.gov.pmrv.api.files.common;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileType {

    DOCX("docx", Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
    BMP("bmp", Set.of("image/bmp", "image/x-ms-bmp"));
    
    private final String simpleType;
    private final Set<String> mimeTypes;
    
}

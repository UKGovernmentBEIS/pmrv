package uk.gov.pmrv.api.files.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "files")
@Data
public class FileTypesProperties {

    private List<String> allowedMimeTypes = new ArrayList<>();
}

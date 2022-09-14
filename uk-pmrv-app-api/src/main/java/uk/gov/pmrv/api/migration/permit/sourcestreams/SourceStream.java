package uk.gov.pmrv.api.migration.permit.sourcestreams;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceStream {

    private String etsAccountId;

    private String reference;
    private String description;
    private String type;
    
}

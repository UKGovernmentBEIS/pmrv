package uk.gov.pmrv.api.migration.permit.sourcestreams;

import lombok.extern.log4j.Log4j2;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
public final class SourceStreamsMapper {

    public static Optional<SourceStreams> constructSourceStreams(List<SourceStream> etsSourceStreams) {
        List<uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream> pmrvSourceStreams = new ArrayList<>();
        for(SourceStream etsSourceStream : etsSourceStreams) {
            SourceStreamType type = SourceStreamUtils.getPmrvType(etsSourceStream.getType());
            
            SourceStreamDescription description = SourceStreamUtils.getPmrvDescription(etsSourceStream.getDescription());
            String otherDescriptionName = null;
            if(description == null) {
                description = SourceStreamDescription.OTHER;
                otherDescriptionName = etsSourceStream.getDescription();
            }
            
            pmrvSourceStreams.add(uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream.builder()
                    .reference(etsSourceStream.getReference())
                    .type(type)
                    .description(description)
                    .otherDescriptionName(otherDescriptionName)
                    .id(UUID.randomUUID().toString())
                    .build());
        }
        
        return Optional.of(SourceStreams.builder()
                .sourceStreams(pmrvSourceStreams)
                .build());
    }
}

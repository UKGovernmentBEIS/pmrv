package uk.gov.pmrv.api.workflow.request.core.domain;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestMetadataRfiable {

    List<LocalDateTime> getRfiResponseDates();
}

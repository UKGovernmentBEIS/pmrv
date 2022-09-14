package uk.gov.pmrv.api.permit.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.ATTACHMENT_NOT_FOUND;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_EMISSION_POINT_SHOULD_EXIST;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;
import uk.gov.pmrv.api.permit.domain.PermitViolation;

@ExtendWith(MockitoExtension.class)
class PermitReferenceServiceTest {

    @InjectMocks
    private PermitReferenceService validator;

    @Mock private FileAttachmentService fileAttachmentService;

    @Test
    void validateExistenceInPermit_whenNotExists_thenViolation() {

        final Optional<Pair<PermitViolation.PermitViolationMessage, List<String>>> permitViolation =
            validator.validateExistenceInPermit(List.of("emissionPoint1", "emissionPoint2"),
                List.of("emissionPoint2", "emissionPoint3"),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);

        assertTrue(permitViolation.isPresent());
        assertEquals(INVALID_EMISSION_POINT, permitViolation.get().getLeft());
        assertEquals(List.of("emissionPoint3"), permitViolation.get().getRight());
    }

    @Test
    void validateExistenceInPermit_whenAllExist_thenOK() {

        final Optional<Pair<PermitViolation.PermitViolationMessage, List<String>>> permitViolation =
            validator.validateExistenceInPermit(List.of("emissionPoint1", "emissionPoint2"),
                List.of("emissionPoint1", "emissionPoint2"),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);

        assertTrue(permitViolation.isEmpty());
    }

    @Test
    void validateAllInPermitAreUsed_whenSomeAreNotUsed_thenViolation() {
        
        final Optional<Pair<PermitViolation.PermitViolationMessage, Map<String, String>>>
            permitViolation = validator.validateAllInPermitAreUsed(
                Map.of("emissionPointId1", "emissionPointRef1",
                    "emissionPointId2", "emissionPointRef2"),
                List.of("emissionPointId1"),
                PermitReferenceService.Rule.EMISSION_POINTS_USED);

        assertTrue(permitViolation.isPresent());
        assertEquals(EMISSION_SUMMARIES_EMISSION_POINT_SHOULD_EXIST, permitViolation.get().getLeft());
        assertEquals(Map.of("emissionPointId2", "emissionPointRef2"), permitViolation.get().getRight());
    }

    @Test
    void validateAllInPermitAreUsed_whenAllAreUsed_thenOK() {
        
        final Optional<Pair<PermitViolation.PermitViolationMessage, Map<String, String>>>
            permitViolation = validator.validateAllInPermitAreUsed(
            Map.of("emissionPointId1", "emissionPointRef1",
                "emissionPointId2", "emissionPointRef2"),
            List.of("emissionPointId1", "emissionPointId2"),
            PermitReferenceService.Rule.EMISSION_POINTS_USED);

        assertTrue(permitViolation.isEmpty());
    }

    @Test
    void validateFilesExist() {
        UUID file1 = UUID.randomUUID();
        UUID file2 = UUID.randomUUID();

        when(fileAttachmentService.fileAttachmentsExist(Set.of(file1.toString(), file2.toString()))).thenReturn(true);

        final Optional<Pair<PermitViolation.PermitViolationMessage, List<String>>> permitViolation = validator
                .validateFilesExist(Set.of(file1, file2), Set.of(file1, file2, UUID.randomUUID()));

        assertTrue(permitViolation.isEmpty());
        verify(fileAttachmentService, times(1))
                .fileAttachmentsExist(Set.of(file1.toString(), file2.toString()));
    }

    @Test
    void validateFilesExist_files_not_exist() {
        UUID file1 = UUID.randomUUID();
        UUID file2 = UUID.randomUUID();

        when(fileAttachmentService.fileAttachmentsExist(Set.of(file1.toString(), file2.toString()))).thenReturn(false);

        final Optional<Pair<PermitViolation.PermitViolationMessage, List<String>>> permitViolation = validator
                .validateFilesExist(Set.of(file1, file2), Set.of(file1, file2, UUID.randomUUID()));

        assertTrue(permitViolation.isPresent());
        assertEquals(ATTACHMENT_NOT_FOUND, permitViolation.get().getLeft());
        verify(fileAttachmentService, times(1))
                .fileAttachmentsExist(Set.of(file1.toString(), file2.toString()));
    }

    @Test
    void validateFilesExist_files_not_in_permit() {
        UUID file1 = UUID.randomUUID();
        UUID file2 = UUID.randomUUID();

        final Optional<Pair<PermitViolation.PermitViolationMessage, List<String>>> permitViolation = validator
                .validateFilesExist(Set.of(file1, file2), Set.of(file1, UUID.randomUUID()));

        assertTrue(permitViolation.isPresent());
        assertEquals(ATTACHMENT_NOT_FOUND, permitViolation.get().getLeft());
        verify(fileAttachmentService, never()).fileAttachmentsExist(anySet());
    }

    @Test
    void validateFilesExist_no_files() {
        final Optional<Pair<PermitViolation.PermitViolationMessage, List<String>>> permitViolation = validator
                .validateFilesExist(Set.of(), Set.of(UUID.randomUUID()));

        assertTrue(permitViolation.isEmpty());
        verify(fileAttachmentService, never()).fileAttachmentsExist(anySet());
    }
}
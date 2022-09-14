package uk.gov.pmrv.api.terms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.terms.domain.Terms;
import uk.gov.pmrv.api.terms.repository.TermsRepository;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermsRepository termsRepository;

    public Terms getLatestTerms() {
        return termsRepository.findLatestTerms();
    }

}

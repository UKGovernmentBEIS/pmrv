package uk.gov.pmrv.api.referencedata.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.repository.CountryRepository;

@Service("countryService")
public class CountryService implements ReferenceDataService<Country>{

	private final CountryRepository countryRepository;
	
	public CountryService(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}
	
	@Override
	public List<Country> getReferenceData() {
		return countryRepository.findAll();
	}

}

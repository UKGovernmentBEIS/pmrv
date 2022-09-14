package uk.gov.pmrv.api.common.config.property;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

	private Client client;
	private Web web;
	private ClamAV clamAV;
	private String competentAuthorityCentralInfo;
	
	@Getter
	@Setter
    public static class Client {
    	private Integer connectTimeout = 10000;
    	private Integer readTimeout = 10000;
    	@NotEmpty @URL
    	private String passwordUrl;
    }

	@Getter
	@Setter
	public static class Web {
		@NotEmpty @URL
		private String url;
	}

	@Getter
	@Setter
	public static class ClamAV {
		@NotEmpty
		private String host;
		@NotNull
		private Integer port;
	}
}

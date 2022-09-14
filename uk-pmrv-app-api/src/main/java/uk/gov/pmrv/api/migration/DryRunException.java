package uk.gov.pmrv.api.migration;

import java.util.List;

public class DryRunException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public DryRunException(List<String> errors) {
        super();
        this.errors = errors;
    }

    private List<String> errors;
    
    public List<String> getErrors() {
        return errors;
    }
}

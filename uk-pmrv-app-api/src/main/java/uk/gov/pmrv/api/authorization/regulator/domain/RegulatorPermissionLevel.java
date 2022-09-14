package uk.gov.pmrv.api.authorization.regulator.domain;

public enum RegulatorPermissionLevel {
    NONE(0),
    VIEW_ONLY(1),
    EXECUTE(2);

    private final Integer rank;

    RegulatorPermissionLevel(Integer rank) {
        this.rank = rank;
    }

    public boolean isLessThan(RegulatorPermissionLevel l) {
        return (this.rank - l.rank) < 0;
    }
}

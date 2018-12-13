package me.exrates.model.enums;

public enum ReportGroupUserRole implements RealCheckableRole {
    ADMIN(true),
    USER(true),
    TRADER(true),
    BOT(false);

    private final Boolean isReal;

    ReportGroupUserRole(Boolean isReal) {
        this.isReal = isReal;
    }

    @Override
    public String getName() {
        return this.name();
    }

    public boolean isReal() {
        return isReal;
    }


}

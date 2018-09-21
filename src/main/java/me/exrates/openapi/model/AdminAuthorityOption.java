package me.exrates.openapi.model;

import me.exrates.openapi.model.enums.AdminAuthority;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class AdminAuthorityOption {
    AdminAuthority adminAuthority;
    Boolean enabled;

    private String adminAuthorityLocalized;

    public AdminAuthority getAdminAuthority() {
        return adminAuthority;
    }

    public void setAdminAuthority(AdminAuthority adminAuthority) {
        this.adminAuthority = adminAuthority;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAdminAuthorityLocalized() {
        return adminAuthorityLocalized;
    }

    public void localize(MessageSource messageSource, Locale locale) {
        adminAuthorityLocalized = adminAuthority.toLocalizedString(messageSource, locale);
    }

    @Override
    public String toString() {
        return "AdminAuthorityOption{" +
                ", adminAuthority=" + adminAuthority +
                ", enabled=" + enabled +
                '}';
    }
}

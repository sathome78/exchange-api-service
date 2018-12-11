package me.exrates.model.form;

import me.exrates.model.AdminAuthorityOption;

import java.util.List;

/**
 * Created by OLEG on 18.11.2016.
 */
public class AuthorityOptionsForm {
    private List<AdminAuthorityOption> options;
    private Integer userId;

    public List<AdminAuthorityOption> getOptions() {
        return options;
    }

    public void setOptions(List<AdminAuthorityOption> options) {
        this.options = options;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

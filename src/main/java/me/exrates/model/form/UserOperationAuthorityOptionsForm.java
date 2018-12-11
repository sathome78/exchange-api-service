package me.exrates.model.form;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.userOperation.UserOperationAuthorityOption;

import java.util.List;

/**
 * @author Vlad Dziubak
 * Date: 30.07.2018
 */
@Getter
@Setter
public class UserOperationAuthorityOptionsForm {
    private List<UserOperationAuthorityOption> options;
    private Integer userId;
}

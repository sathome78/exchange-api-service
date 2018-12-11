package me.exrates.service.userOperation;

import me.exrates.model.userOperation.enums.UserOperationAuthority;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
public interface UserOperationService {

  boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority);


}

package me.exrates.openapi.service.userOperation;

import me.exrates.openapi.model.userOperation.enums.UserOperationAuthority;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
public interface UserOperationService {

  boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority);


}

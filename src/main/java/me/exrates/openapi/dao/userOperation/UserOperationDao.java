package me.exrates.openapi.dao.userOperation;

import me.exrates.openapi.model.userOperation.UserOperationAuthorityOption;
import me.exrates.openapi.model.userOperation.enums.UserOperationAuthority;

import java.util.List;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
public interface UserOperationDao {

  boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority);

  List<UserOperationAuthorityOption> getUserOperationAuthorityOption(Integer userId);

  void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId);


}
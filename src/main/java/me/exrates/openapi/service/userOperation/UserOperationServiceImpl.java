package me.exrates.openapi.service.userOperation;

import lombok.extern.log4j.Log4j2;
import me.exrates.openapi.dao.UserDao;
import me.exrates.openapi.dao.userOperation.UserOperationDao;
import me.exrates.openapi.model.userOperation.enums.UserOperationAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * @author Vlad Dziubak
 * Date: 01.08.2018
 */
@Log4j2
@Service
public class UserOperationServiceImpl implements UserOperationService {

  @Autowired
  private UserOperationDao userOperationDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private MessageSource messageSource;

  @Override
  public boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority) {
      return userOperationDao.getStatusAuthorityForUserByOperation(userId, userOperationAuthority);
  }

}

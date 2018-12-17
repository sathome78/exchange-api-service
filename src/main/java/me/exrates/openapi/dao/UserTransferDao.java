package me.exrates.openapi.dao;

import me.exrates.openapi.model.UserTransfer;
import me.exrates.openapi.model.dto.UserTransferInfoDto;

/**
 * Created by maks on 15.03.2017.
 */
public interface UserTransferDao {

    UserTransfer save(UserTransfer userTransfer);

    UserTransferInfoDto getById(int transactionId);


}

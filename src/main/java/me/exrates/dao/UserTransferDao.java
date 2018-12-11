package me.exrates.dao;

import me.exrates.model.UserTransfer;
import me.exrates.model.dto.UserTransferInfoDto;

/**
 * Created by maks on 15.03.2017.
 */
public interface UserTransferDao {

    UserTransfer save(UserTransfer userTransfer);

    UserTransferInfoDto getById(int transactionId);


}

package me.exrates.service.impl;

import me.exrates.dao.UserTransferDao;
import me.exrates.service.UserTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by maks on 15.03.2017.
 */
@Service
public class UserTransferServiceImpl implements UserTransferService {

    @Autowired
    private UserTransferDao userTransferDao;

}

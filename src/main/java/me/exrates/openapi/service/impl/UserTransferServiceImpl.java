package me.exrates.openapi.service.impl;

import me.exrates.openapi.dao.UserTransferDao;
import me.exrates.openapi.service.UserTransferService;
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

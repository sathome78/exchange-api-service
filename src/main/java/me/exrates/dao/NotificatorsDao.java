package me.exrates.dao;

import me.exrates.model.dto.Notificator;

import java.util.List;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificatorsDao {
    Notificator getById(int id);

    int setEnable(int notificatorId, boolean enable);

    List<Notificator> getAdminDtoByRole(int realUserRoleIdByBusinessRoleList);

    List<Notificator> getAllNotificators();

}

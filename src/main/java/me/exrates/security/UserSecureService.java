package me.exrates.security;

import me.exrates.model.User;
import me.exrates.model.dto.UserShortDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.enums.UserRole;

import java.util.List;
import java.util.Map;

public interface UserSecureService {

    UserShortDto getUserByUsername(String email);

    public List<User> getAllUsers();

public List<User> getUsersByRoles(List<UserRole> listRoles);

    DataTable<List<User>> getUsersByRolesPaginated(List<UserRole> listRoles, Map<String, String> tableParams);

    public UserRole getUserRoles(String email);

    List<String> getUserAuthorities(String email);
}

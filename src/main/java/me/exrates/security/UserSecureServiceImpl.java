package me.exrates.security;

import me.exrates.dao.UserDao;
import me.exrates.model.PagingData;
import me.exrates.model.User;
import me.exrates.model.dto.UserShortDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.enums.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class UserSecureServiceImpl implements UserSecureService {

	@Autowired
    UserDao userDao;

	private static final Logger logger = LogManager.getLogger(UserSecureServiceImpl.class);


	@Override
	public UserShortDto getUserByUsername(String email) {
		return userDao.findShortByEmail(email);
	}

	public List<User> getAllUsers() {
		logger.trace("Begin 'getAllUsers' method");
		return userDao.getAllUsers();
		
	}

	public List<User> getUsersByRoles(List<UserRole> listRoles){
		logger.trace("Begin 'getUsersByRoles' method");
		return  userDao.getUsersByRoles(listRoles);
	}

	@Override
	public DataTable<List<User>> getUsersByRolesPaginated(List<UserRole> listRoles, Map<String, String> tableParams) {
		Integer start = Integer.parseInt(tableParams.getOrDefault("start", "0"));
		Integer length = Integer.parseInt(tableParams.getOrDefault("length", "10"));
		Integer orderColumnIndex = Integer.parseInt(tableParams.getOrDefault("order[0][column]", "0"));
		String orderColumnName = tableParams.getOrDefault("columns[" + orderColumnIndex + "][data]", "id");
		if ("role".equals(orderColumnName)) {
			orderColumnName = "roleid";
		}
		String orderDirection = tableParams.getOrDefault("order[0][dir]", "asc");
		String searchValue = tableParams.get("search[value]");
		PagingData<List<User>> searchResult = userDao.getUsersByRolesPaginated(listRoles, start,
				length, orderColumnName, orderDirection, searchValue);
		DataTable<List<User>> output = new DataTable<>();
		output.setData(searchResult.getData());
		output.setRecordsTotal(searchResult.getTotal());
		output.setRecordsFiltered(searchResult.getFiltered());
		return output;
	}

	public UserRole getUserRoles(String email){
		logger.trace("Begin 'getUserRoles' method");
		return userDao.getUserRoles(email);
	}

	@Override
    public List<String> getUserAuthorities(String email) {
		return userDao.getUserRoleAndAuthorities(email);
	}
}

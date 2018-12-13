package me.exrates.security;

import me.exrates.dao.exception.UserNotFoundException;
import me.exrates.model.dto.UserShortDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserSecureService userSecureService;

	private static final Logger logger = LogManager.getLogger(UserDetailsServiceImpl.class);

    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		logger.trace("Begin 'loadUserByUsername' method");
		org.springframework.security.core.userdetails.User userSpring;
		try {
			UserShortDto person = userSecureService.getUserByUsername(login);
			userSpring = new org.springframework.security.core.userdetails.User(person.getEmail(), person.getPassword(), ifUserAllowed(person), true, true, true,
					getAuthorities(person.getEmail()));
			return userSpring;
		} catch (UserNotFoundException e) {
			throw new UsernameNotFoundException("Несуществующий логин");
		}

   }

    private Collection<GrantedAuthority> getAuthorities(String login)
	{
		logger.trace("Begin 'getAuthorities' method");
        Collection<GrantedAuthority> authList = userSecureService.getUserAuthorities(login).stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        logger.debug(authList);
		return authList;
	}
    
    private boolean ifUserAllowed(UserShortDto user) {
		logger.trace("Begin 'ifUserAllowed' method");
		int userStatus = user.getStatus().getStatus();
		if(userStatus == 2 || userStatus == 4) {
    		return true;
    	}
    	return false;
    }

	}
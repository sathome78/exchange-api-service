package me.exrates.openapi.services;

import lombok.extern.slf4j.Slf4j;
import me.exrates.openapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class AccessPolicyService {

    private Map<String, CopyOnWriteArrayList<LocalDateTime>> userTimes = new ConcurrentHashMap<>();
    private Map<String, Integer> userLimits = new ConcurrentHashMap<>();

    private int attemptsLimit;
    private int timeLimit;

    private final UserRepository userRepository;

    @Autowired
    public AccessPolicyService(@Value("${api.admin.attempts-limit:5}") int attemptsLimit,
                               @Value("${api.admin.time-limit:3600}") int timeLimit,
                               UserRepository userRepository) {
        this.attemptsLimit = attemptsLimit;
        this.timeLimit = timeLimit;
        this.userRepository = userRepository;
    }

    @Scheduled(initialDelay = 30 * 60 * 1000, fixedDelay = 30 * 60 * 1000)
    public void clearExpiredRequests() {
        log.debug(">> clearExpiredRequests");

        new HashMap<>(userTimes).forEach((k, v) -> {
            if (v.stream().noneMatch(p -> p.isAfter(LocalDateTime.now().minusSeconds(timeLimit)))) {
                userTimes.remove(k);
                log.debug("Removed from cache: " + k);
            }
        });
    }

    public void registerRequest() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        userTimes.putIfAbsent(userEmail, new CopyOnWriteArrayList<>());
        userTimes.get(userEmail).add(LocalDateTime.now());
    }

    public boolean isLimitExceed() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        //todo: delete
        if (userEmail.equals("anonymousUser")) {
            return false;
        }
        LocalDateTime beginTime = LocalDateTime.now().minusSeconds(timeLimit);
        Integer limit = getRequestLimit(userEmail);

        List<LocalDateTime> list = userTimes.get(userEmail);
        if (isNull(list)) {
            return false;
        } else {
            long counter = list.stream().filter(p -> p.isAfter(beginTime)).count();
            return counter > limit;
        }
    }

    @Transactional
    public void setRequestLimit(String userEmail, Integer limit) {
        Integer requestsLimit = userRepository.getRequestsLimit(userEmail);

        if (requestsLimit != 0) {
            userRepository.updateRequestsLimit(userEmail, limit);
        } else {
            userRepository.setRequestsLimit(userEmail, limit);
        }
        userLimits.put(userEmail, limit);
        userTimes.remove(userEmail);
    }

    @Transactional
    public Integer getRequestLimit(String userEmail) {
        if (userLimits.containsKey(userEmail)) {
            return userLimits.get(userEmail);
        } else {
            Integer limit = userRepository.getRequestsLimit(userEmail);
            if (limit == 0) {
                userRepository.setRequestsLimit(userEmail, attemptsLimit);
                limit = attemptsLimit;
            }
            userLimits.put(userEmail, limit);
            return limit;
        }
    }

    @Transactional
    public void enableApiForUser(String userEmail) {
        userRepository.enableApiForUser(userEmail);
    }

    @Transactional
    public void disableApiForUser(String userEmail) {
        userRepository.disableApiForUser(userEmail);
    }

    @Transactional
    public void enableApiForAll() {
        userRepository.enableApiForAll();
    }

    @Transactional
    public void disableApiForAll() {
        userRepository.disableApiForAll();
    }

    public Map<String, Integer> getUserLimits() {
        return Collections.unmodifiableMap(userLimits);
    }

    @Transactional(readOnly = true)
    public boolean isEnabled() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        //todo: delete
        if (userEmail.equals("anonymousUser")) {
            return true;
        }
        return userRepository.isEnabled(userEmail);
    }
}

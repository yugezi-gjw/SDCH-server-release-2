package com.varian.oiscn.base.user;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.varian.oiscn.core.user.UserContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Authentication Cache Class.<br>
 */
@Singleton
@Slf4j
@Getter
public class AuthenticationCache {


    /**
     * Concurrent Token Cache
     */
    private final Cache<String, UserContext> tokenCache;
    /**
     * Token, User Name List of Before Removing Resource Lock
     */
    protected Map<String, String> oldTokenUsernameMap;

    /**
     * Constructor.<br>
     * @param defaultTokenCacheTimeoutInMinutes Default Token Cache Timeout InMinutes
     */
    public AuthenticationCache(int defaultTokenCacheTimeoutInMinutes){
        tokenCache = CacheBuilder.newBuilder().expireAfterAccess(defaultTokenCacheTimeoutInMinutes, TimeUnit.MINUTES).build();
        oldTokenUsernameMap = new ConcurrentHashMap<>();
    }

    /**
     * Put new Token, UserContext Pair into cache.<br>
     * @param token Token
     * @param userContext UserContext
     */
    public void put(@NotNull String token, @NotNull UserContext userContext){
        tokenCache.put(token, userContext);
    }

    /**
     * Remove Token Cache.<br>
     * @param token Token
     */
    public void remove(@NotNull String token) {
        UserContext userContext = get(token);
        if (userContext != null) {
            tokenCache.invalidate(token);
        }
    }

    /**
     * Return UserContext.<br>
     * @param token Token
     * @return UserContext
     */
    public UserContext get(@NotNull String token){
        return tokenCache.getIfPresent(token);
    }

    /**
     * Return the removed user name list (Subtract from last cache list).<br>
     *
     * @return removed user name list
     */
    public synchronized List<String> getRemovedUsernameList() {
        List<String> removedUsernameList = new ArrayList<>();
        final ConcurrentMap<String, UserContext> cacheMap = tokenCache.asMap();
        // add user name to removedUsernameList which not in current cache, and in oldTokenUsernameMap
        oldTokenUsernameMap.forEach((token, oldUsername) -> {
            if (!cacheMap.containsKey(token)) {
                // Expired or logout user.
                removedUsernameList.add(oldUsername);
                // remove from old map.
                oldTokenUsernameMap.remove(token);
            }
        });

        // add new user name to oldTokenUsernameMap
        cacheMap.forEach((token, userContext) -> {
            if (!oldTokenUsernameMap.containsKey(token)) {
                oldTokenUsernameMap.put(token, userContext.getName());
            }
        });
        return removedUsernameList;
    }
}

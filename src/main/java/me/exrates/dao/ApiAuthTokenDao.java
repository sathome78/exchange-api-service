package me.exrates.dao;

import me.exrates.model.ApiAuthToken;

import java.util.Optional;

public interface ApiAuthTokenDao {

    String INSERT_API_AUTH_TOKEN = "INSERT INTO API_AUTH_TOKEN(username, value) VALUES(:username, :value)";

    String SELECT_TOKEN_BY_ID = "SELECT id, username, value, last_request FROM API_AUTH_TOKEN WHERE id = :id";

}

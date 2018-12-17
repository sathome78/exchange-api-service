package me.exrates.openapi.dao;

public interface TemporalTokenDao {

    String UPDATE_BY_VALUE = "UPDATE TEMPORAL_TOKEN token SET token.already_used=TRUE WHERE token.value=:value and token.user_id=:user_id and token.id=:id";

    String DELETE_BY_VALUE = "DELETE FROM TEMPORAL_TOKEN WHERE TEMPORAL_TOKEN.value=:token_value";

}

package me.exrates.model.dto;

import me.exrates.model.enums.ChatLang;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class ChatDto {

    private Long id;
    private Integer userId;
    private String body;
    private String nickname;
    private ChatLang lang;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ChatLang getLang() {
        return lang;
    }

    public void setLang(ChatLang lang) {
        this.lang = lang;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

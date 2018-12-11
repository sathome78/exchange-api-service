package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.serializer.LocalDateSerializer;

import java.time.LocalDate;

/**
 * Created by Valk on 27.05.2016.
 */
public class News implements Cloneable{
    private Integer id;
    private String title;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private String brief;
    private String resource;
    private String content;
    private String newsVariant;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isActive;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /*getters setters*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNewsVariant() {
        return newsVariant;
    }

    public void setNewsVariant(String newsVariant) {
        this.newsVariant = newsVariant;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}

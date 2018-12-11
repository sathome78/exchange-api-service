package me.exrates.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Created by ajet on 03.11.2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Comment {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime editTime;
    private User creator;
    private String comment;
    private boolean messageSent;
    private int id;
    private User user;
    private UserCommentTopicEnum userCommentTopic;
    private boolean isEditable;
}

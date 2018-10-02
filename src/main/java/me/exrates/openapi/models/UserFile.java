package me.exrates.openapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.nio.file.Path;

@Data
@ToString(exclude = {"path"})
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class UserFile {

    private int id;
    private int userId;
    private Path path;
}

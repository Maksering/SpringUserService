package springApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "UserDTO")
public class UserDTO {
    @Schema(description = "Уникальный идентификатор пользователя",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "1"
    )
    private Integer id;
    @Schema(description = "Имя пользователя", example = "Иван")
    private String name;
    @Schema(description = "Электронная почта пользователя", example = "example@example.com")
    private String email;
    @Schema(description = "Возраст пользователся", example = "20")
    private Integer age;
    @Schema(description = "Дата создания пользователя",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "2025-01-01 12:00:00"
    )
    private LocalDateTime created_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}

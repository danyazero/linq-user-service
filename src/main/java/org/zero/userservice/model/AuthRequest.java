package org.zero.userservice.model;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record AuthRequest(
        @Length(min = 12, max = 12, message = "Некорректний формат номеру телефону")
        @Pattern(regexp = "[0-9]{12}", message = "Номер телефону може містити лише цифри")
        String phone,
        @NotBlank(message = "Пароль не може бути пустим")
        @Length(min = 4, message = "Мінімальна довжина паролю 4 символи")
        String password,
        @NotNull
        boolean rememberMe
) {
}

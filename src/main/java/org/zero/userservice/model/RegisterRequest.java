package org.zero.userservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.zero.userservice.utils.SHAEncoder;

public record RegisterRequest(
        @JsonProperty("_IK")
        String idempotencyKey,
        @NotBlank
        @Length(min = 2, max = 64, message = "Некорректна довжина імені")
//        @Pattern(regexp = "\s+", message = "Імʼя може містити лише українські літери")
        String firstName,
        @NotBlank
        @Length(min = 2, max = 64, message = "Некорректна довжина фамілії")
//        @Pattern(regexp = "\s+", message = "Фамілія може містити лише українські літери")
        String lastName,
        @NotBlank
        @Length(min = 2, max = 64, message = "Некорректна довжина по-батькові")
//        @Pattern(regexp = "\s+", message = "По-батькові може містити лише українські літери")
        String middleName,
        @NotBlank
        @Length(min = 12, max = 12, message = "Некорректний формат номеру телефону")
        @Pattern(regexp = "[0-9]+", message = "Номер телефону може містити лише цифри")
        String phone,
        @NotBlank
        @NotBlank(message = "Пароль не може бути пустим")
        @Length(min = 4, message = "Мінімальна довжина паролю 4 символи")
        String password,
        @NotBlank
        @NotBlank(message = "Пароль не може бути пустим")
        @Length(min = 4, message = "Мінімальна довжина паролю 4 символи")
        String passwordRepeat
) {
}

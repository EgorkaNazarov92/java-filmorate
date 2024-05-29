package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
	private Long id;
	@NonNull
	@Email(regexp = ".+[@].+[\\.].+")
	private String email;
	@NonNull
	private String login;
	private String name;
	private LocalDate birthday;
}

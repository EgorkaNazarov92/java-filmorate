package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
	private Set<Long> friends = new HashSet<>();
}

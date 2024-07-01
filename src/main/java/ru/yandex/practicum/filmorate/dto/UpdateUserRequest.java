package ru.yandex.practicum.filmorate.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
	private String name;
	private String email;
	private String login;
	private LocalDate birhday;

	public boolean hasName() {
		return ! (name == null || name.isBlank());
	}

	public boolean hasEmail() {
		return ! (email == null || email.isBlank());
	}

	public boolean hasLogin() {
		return ! (login == null || login.isBlank());
	}

	public boolean hasBirhday() {
		return ! (birhday == null);
	}
}

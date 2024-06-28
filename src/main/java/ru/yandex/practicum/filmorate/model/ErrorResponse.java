package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class ErrorResponse {
	// название ошибки
	String error;
	// подробное описание
	String description;

	public ErrorResponse(String error, String description) {
		this.error = error;
		this.description = description;
	}
}

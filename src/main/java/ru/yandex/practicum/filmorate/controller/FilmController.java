package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

	private final Map<Long, Film> films = new HashMap<>();

	private static final LocalDate startReleaseDate = LocalDate
			.parse("28.12.1895", DateTimeFormatter.ofPattern("dd.MM.yyyy"));

	private static final Logger log = LoggerFactory.getLogger(FilmController.class);

	@GetMapping
	public Collection<Film> findAll() {
		return films.values();
	}

	@PostMapping
	public Film create(@RequestBody Film film) {
		log.info("Request: {}", film);
		// проверяем выполнение необходимых условий
		validateFilm(film);
		// формируем дополнительные данные
		film.setId(getNextId());
		// сохраняем новую публикацию в памяти приложения
		films.put(film.getId(), film);
		log.info("FilmId: {}", film.getId());
		return film;
	}

	@PutMapping
	public Film update(@RequestBody Film newFilm) {
		log.info("Request: {}", newFilm);
		// проверяем необходимые условия
		if (newFilm.getId() == null) {
			log.warn("Film have not got id: {}", newFilm);
			throw new ValidationException("Id должен быть указан");
		}
		validateFilm(newFilm);
		if (films.containsKey(newFilm.getId())) {
			Film oldFilm = films.get(newFilm.getId());
			// если публикация найдена и все условия соблюдены, обновляем её содержимое
			if (newFilm.getDescription() != null) oldFilm.setDescription(newFilm.getDescription());
			if (newFilm.getName() != null) oldFilm.setName(newFilm.getName());
			if (newFilm.getReleaseDate() != null) oldFilm.setReleaseDate(newFilm.getReleaseDate());
			if (newFilm.getDuration() != null) oldFilm.setDuration(newFilm.getDuration());
			log.info("Update Film: {}", oldFilm);
			return oldFilm;
		}
		log.warn("Film with this id, does not exist: {}", films.values());
		throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
	}

	// вспомогательный метод для генерации идентификатора нового поста
	private long getNextId() {
		long currentMaxId = films.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}

	private void validateFilm(Film film) {
		if (film.getName() == null || film.getName().isBlank()) {
			log.warn("Film have not got name: {}", film);
			throw new ValidationException("Название фильма не может быть пустым");
		}
		if (film.getDescription() != null && film.getDescription().length() > 200) {
			log.warn("Description is more 200 symbols: {}", film.getDescription());
			throw new ValidationException("максимальная длина описания — 200 символов");
		}
		if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(startReleaseDate)) {
			log.warn("Film date not correct: {}", film.getReleaseDate());
			throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
		}
		if (film.getDuration() != null && film.getDuration() <= 0) {
			log.warn("Film duration not correct: {}", film.getDuration());
			throw new ValidationException("продолжительность фильма должна быть положительным числом");
		}
	}
}

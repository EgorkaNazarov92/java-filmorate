package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;

@Service
public class FilmService {
	@Autowired
	@Qualifier("FilmDbStorage")
	private FilmStorage filmStorage;
	@Autowired
	@Qualifier("UserDbStorage")
	private UserStorage userStorage;

	private static final LocalDate startReleaseDate = LocalDate
			.parse("28.12.1895", DateTimeFormatter.ofPattern("dd.MM.yyyy"));

	private static final Logger log = LoggerFactory.getLogger(FilmController.class);


	public Film getFilm(Long id) {
		return filmStorage.getFilm(id);
	}

	public Collection<Film> getFilms() {
		return filmStorage.getFilms();
	}

	public Collection<Film> getPopular(int count) {
		Collection<Film> popularFilms = filmStorage.getFilms().stream()
				.sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
				.toList();
		if (popularFilms.size() < count) count = popularFilms.size();
		return popularFilms.stream().toList().subList(0, count);
	}

	public Film addFilm(Film film) {
		log.info("Request: {}", film);
		// проверяем выполнение необходимых условий
		validateFilm(film);
		// сохраняем новую публикацию в памяти приложения
		film = filmStorage.addFilm(film);
		log.info("FilmId: {}", film.getId());
		return film;
	}

	public Film changeFilm(Film newFilm) {
		log.info("Request: {}", newFilm);
		validateFilm(newFilm);
		newFilm = filmStorage.changeFilm(newFilm);
		log.info("Update Film: {}", newFilm);
		return newFilm;
	}

	public void deleteFilm(Long id) {
		filmStorage.deleteFilm(id);
	}

	public void addLike(Long filmId, Long userId) {
		userStorage.getUser(userId);
		filmStorage.addLike(filmId, userId);
	}

	public void deleteLike(Long filmId, Long userId) {
		userStorage.getUser(userId);
		filmStorage.deleteLike(filmId, userId);
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

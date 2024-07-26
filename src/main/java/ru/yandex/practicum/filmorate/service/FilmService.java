package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
	private final FilmStorage filmStorage;

	private final UserStorage userStorage;

	private final DirectorStorage directorStorage;

	private final EventStorage eventStorage;

	public FilmService(
			@Qualifier("FilmDbStorage") FilmStorage filmStorage,
			@Qualifier("UserDbStorage") UserStorage userStorage,
			@Qualifier("DirectorDbStorage") DirectorStorage directorStorage,
			EventStorage eventStorage
	) {
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
		this.directorStorage = directorStorage;
		this.eventStorage = eventStorage;
	}

	private static final LocalDate startReleaseDate = LocalDate
			.parse("28.12.1895", DateTimeFormatter.ofPattern("dd.MM.yyyy"));

	private static final Logger log = LoggerFactory.getLogger(FilmController.class);


	public Film getFilm(Long id) {
		return filmStorage.getFilm(id);
	}

	public Collection<Film> getFilms() {
		return filmStorage.getFilms();
	}

	public Collection<Film> getPopularByYear(int count, Integer genreId, Integer year) {
		Collection<Film> popularFilms = filmStorage.getFilms().stream()
				.filter(film -> (genreId == null || film.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId))))
				.filter(film -> (year == null || film.getReleaseDate().getYear() == year))
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
		Film film = getFilm(filmId);
		if (!film.getLikes().contains(userId)) {
			filmStorage.addLike(filmId, userId);
			Event event = Event.builder()
					.userId(userId)
					.entityId(filmId)
					.timestamp(Instant.now().toEpochMilli())
					.eventType(Event.EventType.LIKE)
					.operation(Event.Operation.ADD)
					.build();
			eventStorage.addEvent(event);
		}
	}

	public void deleteLike(Long filmId, Long userId) {
		userStorage.getUser(userId);
		Film film = getFilm(filmId);
		if (film.getLikes().contains(userId)) {
			filmStorage.deleteLike(filmId, userId);
			Event event = Event.builder()
					.userId(userId)
					.entityId(filmId)
					.timestamp(Instant.now().toEpochMilli())
					.eventType(Event.EventType.LIKE)
					.operation(Event.Operation.REMOVE)
					.build();
			eventStorage.addEvent(event);
		}
	}

	public Collection<Film> getSortedDirectorsFilms(Long directorId, String sortBy) {
		directorStorage.getDirector(directorId);
		if (sortBy.equals("year")) {
			return filmStorage.getFilms().stream()
					.filter(film -> film.getDirectors().stream()
							.anyMatch(director -> director.getId().equals(directorId)))
					.sorted(Comparator.comparing(film -> film.getReleaseDate().getYear()))
					.collect(Collectors.toList());
		} else {
			return filmStorage.getFilms().stream()
					.filter(film -> film.getDirectors().stream()
							.anyMatch(director -> director.getId().equals(directorId)))
					.sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
					.collect(Collectors.toList());
		}

	}

	public Collection<Film> getCommonFilms(Long userId, Long friendId) {
		return filmStorage.getFilms().stream()
				.filter(film -> film.getLikes().contains(userId)
						&& film.getLikes().contains(friendId))
				.sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
				.collect(Collectors.toList());
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

	public Collection<Film> search(String query, String by) {
		List<String> acceptedValues = List.of("title", "director");
		List<String> byList = new ArrayList<>();
		if (by.contains(",")) {
			String[] split = by.split(",");
			for (String s : split) {
				if (!s.isEmpty() && !s.isBlank() && acceptedValues.contains(s)) {
					byList.add(s);
				} else {
					throw new ValidationException("Передан некорретный параметр by = " + s);
				}
			}
		} else {
			if (acceptedValues.contains(by)) {
				byList.add(by);
			} else {
				throw new ValidationException("Передан некорретный параметр by = " + by);
			}
		}
		return filmStorage.search(query, byList)
				.stream()
				.sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
				.toList();
	}
}

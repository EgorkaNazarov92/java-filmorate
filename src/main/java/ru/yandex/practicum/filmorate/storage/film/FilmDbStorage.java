package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("FilmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
	private FilmRepository filmRepository;

	private EventStorage eventStorage;

	@Override
	public Film getFilm(Long id) {
		Optional<Film> film = filmRepository.getFilm(id);
		if (film.isEmpty()) {
			throw new NotFoundException("Фильм с id = " + id + " не найден");
		}
		return film.get();
	}

	@Override
	public Collection<Film> getFilms() {
		return filmRepository.getFilms();
	}

	@Override
	public Film addFilm(Film film) {
		Film newFilm = filmRepository.addFilm(film);
		newFilm.getGenres()
				.forEach(genre -> filmRepository.addGenre(newFilm.getId(), genre.getId()));
		newFilm.getDirector()
				.stream()
				.forEach(director -> filmRepository.addDirector(newFilm.getId(), director.getId()));
		return newFilm;
	}

	@Override
	public void deleteFilm(Long id) {
		getFilm(id);
		filmRepository.deleteFilm(id);
	}

	@Override
	public Film changeFilm(Film film) {
		getFilm(film.getId());
		Film newFilm = filmRepository.changeFilm(film);
		filmRepository.deleteGenres(film.getId());
		film.getGenres()
				.forEach(genre -> filmRepository.addGenre(newFilm.getId(), genre.getId()));
		return filmRepository.changeFilm(film);
	}

	@Override
	public void addLike(Long filmId, Long userId) {
		getFilm(filmId);
		filmRepository.addLike(filmId, userId);
		Event event = Event.builder()
				.userId(userId)
				.entityId(filmId)
				.timestamp(Instant.now().toEpochMilli())
				.eventType(Event.EventType.LIKE)
				.operation(Event.Operation.ADD)
				.build();
		eventStorage.addEvent(event);
	}

	@Override
	public void deleteLike(Long filmId, Long userId) {
		getFilm(filmId);
		filmRepository.deleteLike(filmId, userId);
		Event event = Event.builder()
				.userId(userId)
				.entityId(filmId)
				.timestamp(Instant.now().toEpochMilli())
				.eventType(Event.EventType.LIKE)
				.operation(Event.Operation.REMOVE)
				.build();
		eventStorage.addEvent(event);
	}

	@Override
	public Collection<Film> getRecommendedFilms(Long userId) {
		return filmRepository.getRecommendedFilms(userId);
	}
}

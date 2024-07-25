package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
	Film getFilm(Long id);

	Collection<Film> getFilms();

	Film addFilm(Film film);

	void deleteFilm(Long id);

	Film changeFilm(Film film);

	void addLike(Long filmId, Long userId);

	void deleteLike(Long filmId, Long userId);

	Collection<Film> getRecommendedFilms(Long userId);
}

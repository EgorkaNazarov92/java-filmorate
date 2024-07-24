package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
	@Autowired
	private FilmRepository filmRepository;

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
				.stream()
				.forEach(genre -> filmRepository.addGenre(newFilm.getId(), genre.getId()));
		newFilm.getDirector()
				.stream()
				.forEach(director -> filmRepository.addDirector(newFilm.getId(), director.getId()));
		return newFilm;
	}

	@Override
	public void removeFilm(Long id) {
		getFilm(id);
		filmRepository.removeFilm(id);
	}

	@Override
	public Film changeFilm(Film film) {
		getFilm(film.getId());
		return filmRepository.changeFilm(film);
	}

	@Override
	public void addLike(Long filmId, Long userId) {
		getFilm(filmId);
		filmRepository.addLike(filmId, userId);
	}

	@Override
	public void deleteLike(Long filmId, Long userId) {
		getFilm(filmId);
		filmRepository.deleteLike(filmId, userId);
	}
}

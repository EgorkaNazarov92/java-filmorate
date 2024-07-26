package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("FilmDbStorage")
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
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
				.forEach(genre -> filmRepository.addGenre(newFilm.getId(), genre.getId()));
		newFilm.getDirectors()
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
		filmRepository.changeFilm(film);
		filmRepository.deleteGenres(film.getId());
		film.getGenres()
				.forEach(genre -> filmRepository.addGenre(film.getId(), genre.getId()));
		filmRepository.deleteDirectors(film.getId());
		film.getDirectors()
				.forEach(director -> filmRepository.addDirector(film.getId(), director.getId()));
		return getFilm(film.getId());
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

	@Override
	public Collection<Film> getRecommendedFilms(Long userId) {
		return filmRepository.getRecommendedFilms(userId);
	}

    @Override
    public Collection<Film> search(String query, List<String> by) {
        return filmRepository.search(query, by);
    }
}

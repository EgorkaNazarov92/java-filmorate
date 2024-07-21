package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

	private final Map<Long, Film> films = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

	@Override
	public Film getFilm(Long id) {
		Optional<Film> film = Optional.ofNullable(films.get(id));
		if (film.isEmpty()) {
			throw new NotFoundException("Фильм с id = " + id + " не найден");
		}
		return film.get();
	}

	@Override
	public Collection<Film> getFilms() {
		return films.values();
	}

	@Override
	public Film addFilm(Film film) {
		// формируем дополнительные данные
		film.setId(getNextId());
		// сохраняем новую публикацию в памяти приложения
		films.put(film.getId(), film);
		return film;
	}

	@Override
	public void removeFilm(Long id) {
		if (films.containsKey(id)) films.remove(id);
		else {
			throw new NotFoundException("Фильм с id = " + id + " не найден");
		}

	}

	@Override
	public Film changeFilm(Film newFilm) {
		// проверяем необходимые условия
		if (newFilm.getId() == null) {
			log.warn("Film have not got id: {}", newFilm);
			throw new ValidationException("Id должен быть указан");
		}
		if (films.containsKey(newFilm.getId())) {
			Film oldFilm = films.get(newFilm.getId());
			// если публикация найдена и все условия соблюдены, обновляем её содержимое
			if (newFilm.getDescription() != null) oldFilm.setDescription(newFilm.getDescription());
			if (newFilm.getName() != null) oldFilm.setName(newFilm.getName());
			if (newFilm.getReleaseDate() != null) oldFilm.setReleaseDate(newFilm.getReleaseDate());
			if (newFilm.getDuration() != null) oldFilm.setDuration(newFilm.getDuration());
			return oldFilm;
		} else {
			throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
		}
	}

	@Override
	public void addLike(Long filmId, Long userId) {
		if (!films.containsKey(filmId)) {
			throw new NotFoundException("Фильм с id = " + filmId + " не найден");
		}
		films.get(filmId).getLikes().add(userId);
	}

	@Override
	public void deleteLike(Long filmId, Long userId) {
		if (!films.containsKey(filmId)) {
			throw new NotFoundException("Фильм с id = " + filmId + " не найден");
		}
		films.get(filmId).getLikes().remove(userId);
	}

    @Override
    public Collection<Film> search(String query, List<String> by) {
        return films.values()
                .stream()
                .filter(film -> film.getName().toLowerCase().contains(query))
                .toList();
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
}

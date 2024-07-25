package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

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
    public void deleteFilm(Long id) {
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
    public Collection<Film> getRecommendedFilms(Long userId) {
        Map<Long, Integer> commonLikesCount = new HashMap<>();
        Set<Long> userLikedFilms = new HashSet<>();

        for (Film film : films.values()) {
            if (film.getLikes().contains(userId)) {
                userLikedFilms.add(film.getId());
            }
        }

        for (Film film : films.values()) {
            for (Long likeUserId : film.getLikes()) {
                if (!likeUserId.equals(userId) && userLikedFilms.contains(film.getId())) {
                    commonLikesCount.put(likeUserId, commonLikesCount.getOrDefault(likeUserId, 0) + 1);
                }
            }
        }
        Long mostSimilarUserId = commonLikesCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostSimilarUserId == null) {
            return Collections.emptyList();
        }

        Set<Long> recommendedFilmIds = new HashSet<>();
        for (Film film : films.values()) {
            if (film.getLikes().contains(mostSimilarUserId) && !film.getLikes().contains(userId)) {
                recommendedFilmIds.add(film.getId());
            }
        }

        List<Film> recommendedFilms = new ArrayList<>();
        for (Long filmId : recommendedFilmIds) {
            recommendedFilms.add(films.get(filmId));
        }

        return recommendedFilms;
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

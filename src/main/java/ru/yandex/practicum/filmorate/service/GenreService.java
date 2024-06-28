package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Service
public class GenreService {
	private final GenreRepository genreRepository;

	public GenreService(GenreRepository genreRepository) {
		this.genreRepository = genreRepository;
	}

	public Genre getGenre(Integer id) {
		Optional<Genre> genre = genreRepository.getGenre(id);
		if (genre.isEmpty()) throw new NotFoundException("Genre с id = " + id + " не найден");
		return genre.get();
	}

	public Collection<Genre> getGenres() {
		return genreRepository.getGenres();
	}
}

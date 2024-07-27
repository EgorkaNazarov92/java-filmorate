package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class DirectorService {

	@Autowired
	@Qualifier("DirectorDbStorage")
	private DirectorStorage directorStorage;

	public Director getDirector(long id) {
		Optional<Director> director = directorStorage.getDirector(id);
		if (director.isEmpty()) {
			throw new NotFoundException("Director с id = " + id + " не найден");
		}
		return director.orElseThrow();
	}

	public Collection<Director> getDirectors() {
		return directorStorage.getDirectors();
	}

	public Director addDirector(Director director) {
		if (director.getName().isEmpty() || director.getName().isBlank()) {
			throw new ValidationException("Имя не должно быть пустым");
		}
		return directorStorage.addDirector(director);
	}

	public Director changeDirector(Director director) {
		getDirector(director.getId());
		return directorStorage.changeDirector(director);
	}

	public void deleteDirector(long id) {
		getDirector(id);
		directorStorage.deleteDirector(id);
	}
}

package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
	Optional<Director> getDirector(long id);

	Collection<Director> getDirectors();

	Director addDirector(Director director);

	Director changeDirector(Director director);

	void deleteDirector(long id);
}

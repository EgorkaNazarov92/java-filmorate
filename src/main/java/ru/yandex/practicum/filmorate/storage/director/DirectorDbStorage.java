package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("DirectorDbStorage")
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
	private final DirectorRepository directorRepository;

	@Override
	public Optional<Director> getDirector(long id) {
		Optional<Director> director = directorRepository.getDirector(id);
		if (director.isEmpty()) {
			throw new NotFoundException("Режисер с id = " + id + " не найден");
		}
		return director;
	}

	@Override
	public Collection<Director> getDirectors() {
		return directorRepository.getDirectors();
	}

	@Override
	public Director addDirector(Director director) {
		return directorRepository.addDirector(director);
	}

	@Override
	public Director changeDirector(Director director) {
		return directorRepository.updateDirector(director);
	}

	@Override
	public void deleteDirector(long id) {
		directorRepository.deleteDirector(id);
	}
}

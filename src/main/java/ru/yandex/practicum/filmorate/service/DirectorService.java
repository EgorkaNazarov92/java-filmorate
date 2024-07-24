package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.dto.ObjectWithNameDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    public Director getDirector(long id) {
        Optional<Director> director = directorRepository.getDirector(id);
        if (director.isEmpty()) {
            throw new NotFoundException("Director с id = " + id + " не найден");
        }
        return director.get();
    }

    public Collection<Director> getDirectors() {
        return directorRepository.getDirectors();
    }

    public Director addDirector(ObjectWithNameDto director) {
        if (director.getName().isEmpty() || director.getName().isBlank()) {
            throw new ValidationException("Имя не должно быть пустым");
        }
        return directorRepository.addDirector(director);
    }

    public Director changeDirector(Director director) {
        getDirector(director.getId());
        return directorRepository.updateDirector(director);
    }

    public void deleteDirector(long id) {
        getDirector(id);
        directorRepository.deleteDirector(id);
    }
}

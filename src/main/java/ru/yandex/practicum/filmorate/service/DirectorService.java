package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    public Director getDirector(int id) {
        Optional<Director> director = directorRepository.getDirector(id);
        if (director.isEmpty()) {
            throw new NotFoundException("Director с id = " + id + " не найден");
        }
        return director.get();
    }

    public Collection<Director> getDirectors() {
        return directorRepository.getDirectors();
    }

    public Director addDirector(Director director) {
        return directorRepository.addDirector(director);
    }

    public Director changeDirector(Director director) {
        return directorRepository.updateDirector(director);
    }

    public void deleteDirector(int id) {
        directorRepository.deleteDirector(id);
    }
}

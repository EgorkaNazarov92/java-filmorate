package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.ObjectWithNameDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable long id) {
        return directorService.getDirector(id);
    }

    @GetMapping
    public Collection<Director> getDirectors() {
        return directorService.getDirectors();
    }

    @PostMapping
    public Director addDirector(@RequestBody ObjectWithNameDto director) {
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return directorService.changeDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable long id) {
        directorService.deleteDirector(id);
    }
}

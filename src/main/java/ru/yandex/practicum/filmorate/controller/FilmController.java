package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
	private final FilmService filmService;
	private final DirectorService directorService;

	@GetMapping("/{id}")
	public Film getFilm(@PathVariable("id") Long id) {
		return filmService.getFilm(id);
	}

	@GetMapping
	public Collection<Film> getFilms() {
		return filmService.getFilms();
	}

	@PostMapping
	public Film create(@RequestBody Film film) {
		System.out.println(film);
		return filmService.addFilm(film);
	}

	@PutMapping
	public Film update(@RequestBody Film newFilm) {
		return filmService.changeFilm(newFilm);
	}

	@PutMapping("/{id}/like/{userId}")
	public void addLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
		filmService.addLike(filmId, userId);
	}

	@DeleteMapping("/{id}/like/{userId}")
	public void deleteLike(@PathVariable("id") Long filmId, @PathVariable("userId") Long userId) {
		filmService.deleteLike(filmId, userId);
	}

	@GetMapping("/popular")
	public Collection<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
		return filmService.getPopular(count);
	}

	@GetMapping("/director/{directorId}")
	public Collection<Film> getSortedDirectorsFilms(@PathVariable("directorId") int directorId,
													@RequestParam String sortBy) {
		return filmService.getSortedDirectorsFilms(directorId, sortBy);
	}

}

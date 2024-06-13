package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private FilmController filmController;
	@Autowired
	private UserController userController;

	@Test
	public void contextLoads() {
		assertThat(filmController).isNotNull();
		assertThat(userController).isNotNull();
	}

	@Test
	public void testAddEmptyFilm() {
		Film film = new Film();
		Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
	}

	@Test
	public void testAddFilm() {
		Film film = new Film();
		film.setName("Однажды в вегасе");
		film.setDescription("Комедия.");
		film.setDuration(1000);
		film.setReleaseDate(LocalDate.now());
		filmController.create(film);
		Collection<Film> films = filmController.getFilms();
		Optional<Film> optFilm = films.stream().findFirst();
		assertTrue(optFilm.isPresent());
		Film returnFilm = optFilm.get();
		assertEquals(returnFilm.getName(), film.getName());
		assertEquals(returnFilm.getDuration(), film.getDuration());
		assertEquals(returnFilm.getDescription(), film.getDescription());
		assertEquals(returnFilm.getReleaseDate(), film.getReleaseDate());
	}

	@Test
	public void testAddFilmUncorrectRelease() {
		Film film = new Film();
		film.setName("Однажды в вегасе");
		film.setDescription("Комедия.");
		film.setDuration(1000);
		film.setReleaseDate(LocalDate.parse("28.12.1795", DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
	}

	@Test
	public void testAddFilmUncorrectDuration() {
		Film film = new Film();
		film.setName("Однажды в вегасе");
		film.setDescription("Комедия.");
		film.setDuration(-100);
		film.setReleaseDate(LocalDate.now());
		Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
	}

	@Test
	public void testAddFilmUncorrectDescription() {
		Film film = new Film();
		film.setName("Однажды в вегасе");
		film.setDescription("Очень длинное описание. Очень длинное описание. Очень длинное описание." +
				" Очень длинное описание. Очень длинное описание. Очень длинное описание. Очень длинное описание." +
				" Очень длинное описание. Очень длинное описание. ");
		film.setDuration(100);
		film.setReleaseDate(LocalDate.now());
		Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
	}

	@Test
	public void testAddEmptyUser() {
		User user = new User("", "");
		Assertions.assertThrows(ConstraintViolationException.class, () -> userController.addUser(user));
	}

	@Test
	public void testAddEmptyEmailUser() {
		User user = new User("", "testLogin");
		Assertions.assertThrows(ConstraintViolationException.class, () -> userController.addUser(user));
	}

	@Test
	public void testAddUncorrectEmailUser() {
		User user = new User("asdasd", "testLogin");
		Assertions.assertThrows(ConstraintViolationException.class, () -> userController.addUser(user));
	}

	@Test
	public void testAddUncorrectLoginUser() {
		User user = new User("asdasd@mail.ru", "test Login");
		Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
	}

	@Test
	public void testAddEmptyNameUser() {
		User user = new User("asdasd@mail.ru", "testLogin");
		user = userController.addUser(user);
		assertEquals(user.getLogin(), user.getName());
	}

	@Test
	public void testAddUncorrectBirthdayUser() {
		User user = new User("asdasd@mail.ru", "testLogin");
		user.setBirthday(LocalDate.now().plusDays(1));
		Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));
	}
}

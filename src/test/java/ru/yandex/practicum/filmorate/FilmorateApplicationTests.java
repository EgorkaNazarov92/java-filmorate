package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.extractor.EventExtractor;
import ru.yandex.practicum.filmorate.dal.extractor.FilmExtractor;
import ru.yandex.practicum.filmorate.dal.extractor.UserExtractor;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.event.EventStorageImpl;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = {UserRepository.class, UserDbStorage.class, UserExtractor.class, UserService.class,
	FilmRepository.class, FilmDbStorage.class, FilmExtractor.class, FilmService.class, EventStorageImpl.class, EventRepository.class, EventExtractor.class})
class FilmorateApplicationTests {

	private final FilmService filmService;
	private final UserService userService;

	@Test
	public void contextLoads() {
		assertThat(filmService).isNotNull();
		assertThat(userService).isNotNull();
	}

	@Test
	public void testAddEmptyFilm() {
		Film film = new Film();
		Assertions.assertThrows(ValidationException.class, () -> filmService.addFilm(film));
	}

	@Test
	public void testAddFilm() {
		Film film = new Film();
		film.setName("Однажды в вегасе");
		film.setDescription("Комедия.");
		film.setDuration(1000);
		film.setReleaseDate(LocalDate.now());
		filmService.addFilm(film);
		Collection<Film> films = filmService.getFilms();
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
		Assertions.assertThrows(ValidationException.class, () -> filmService.addFilm(film));
	}

	@Test
	public void testAddFilmUncorrectDuration() {
		Film film = new Film();
		film.setName("Однажды в вегасе");
		film.setDescription("Комедия.");
		film.setDuration(-100);
		film.setReleaseDate(LocalDate.now());
		Assertions.assertThrows(ValidationException.class, () -> filmService.addFilm(film));
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
		Assertions.assertThrows(ValidationException.class, () -> filmService.addFilm(film));
	}

	@Test
	public void testAddEmptyUser() {
		User user = new User("", "");
		Assertions.assertThrows(ValidationException.class, () -> userService.addUser(user));
	}

	@Test
	public void testAddEmptyEmailUser() {
		User user = new User("", "testLogin");
		Assertions.assertThrows(ValidationException.class, () -> userService.addUser(user));
	}

	@Test
	public void testAddUncorrectLoginUser() {
		User user = new User("asdasd@mail.ru", "test Login");
		Assertions.assertThrows(ValidationException.class, () -> userService.addUser(user));
	}

	@Test
	public void testAddEmptyNameUser() {
		User user = new User("asdasd@mail.ru", "testLogin");
		UserDto userDto = userService.addUser(user);
		assertEquals(userDto.getLogin(), userDto.getName());
	}

	@Test
	public void testAddUncorrectBirthdayUser() {
		User user = new User("asdasd@mail.ru", "testLogin");
		user.setBirthday(LocalDate.now().plusDays(1));
		Assertions.assertThrows(ValidationException.class, () -> userService.addUser(user));
	}
}

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {

	private final Map<Long, User> users = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping
	public Collection<User> findAll() {
		return users.values();
	}

	@PostMapping
	public User create(@Valid @RequestBody User user) {
		log.debug("Request: {}", user);
		// проверяем выполнение необходимых условий
		validateUser(user);
		// формируем дополнительные данные
		user.setId(getNextId());
		if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
		// сохраняем новую публикацию в памяти приложения
		users.put(user.getId(), user);
		log.debug("List of all films: {}", users.values());
		return user;
	}

	@PutMapping
	public User update(@Valid @RequestBody User newUser) {
		// проверяем необходимые условия
		if (newUser.getId() == null) {
			log.warn("User have not got id: {}", newUser);
			throw new ValidationException("Id должен быть указан");
		}
		validateUser(newUser);
		if (users.containsKey(newUser.getId())) {
			User oldUser = users.get(newUser.getId());
			// если публикация найдена и все условия соблюдены, обновляем её содержимое
			if (newUser.getName() != null) oldUser.setName(newUser.getName());
			if (newUser.getBirthday() != null) oldUser.setBirthday(newUser.getBirthday());
			oldUser.setEmail(newUser.getEmail());
			oldUser.setLogin(newUser.getLogin());
			log.debug("Update user: {}", oldUser);
			return oldUser;
		}
		log.warn("User with this id, does not exist: {}", users.values());
		throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
	}

	// вспомогательный метод для генерации идентификатора нового поста
	private long getNextId() {
		long currentMaxId = users.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}

	private void validateUser(User user) {
		if (user.getLogin().contains(" ") || user.getLogin().isEmpty()) {
			log.warn("User login is not correct: {}", user.getLogin());
			throw new ValidationException("логин не может быть пустым и содержать пробелы");
		}
		if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
			log.warn("User birthday is not correct: {}", user.getBirthday());
			throw new ValidationException("дата рождения не может быть в будущем");
		}
	}
}

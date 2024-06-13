package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class UserService {
	protected final InMemoryUserStorage inMemoryUserStorage;

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	public UserService(InMemoryUserStorage inMemoryUserStorage) {
		this.inMemoryUserStorage = inMemoryUserStorage;
	}

	public Collection<User> getUsers() {
		return inMemoryUserStorage.getUsers();
	}

	public Collection<User> getFriends(Long id) {
		return inMemoryUserStorage.getFriends(id);
	}

	public Collection<User> getMutualFriends(Long id, Long otherId) {
		return inMemoryUserStorage.getMutualFriends(id, otherId);
	}

	public User addUser(User user) {
		log.info("Request: {}", user);
		// проверяем выполнение необходимых условий
		validateUser(user);
		user = inMemoryUserStorage.addUser(user);
		log.info("UserId: {}", user.getId());
		return user;
	}

	public User changeUser(User newUser) {
		log.info("Request: {}", newUser);
		validateUser(newUser);
		newUser = inMemoryUserStorage.changeUser(newUser);
		log.info("Update user: {}", newUser);
		return newUser;
	}

	public void removeUser(Long id) {
		inMemoryUserStorage.removeUser(id);
	}

	public void addFriend(Long userId, Long friendId) {
		inMemoryUserStorage.addFriend(userId, friendId);
	}

	public void deleteFriend(Long userId, Long friendId) {
		inMemoryUserStorage.deleteFriend(userId, friendId);
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

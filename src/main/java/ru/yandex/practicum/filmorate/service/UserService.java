package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {
	@Autowired
	@Qualifier("UserDbStorage")
	private UserStorage userStorage;

	private static final Logger log = LoggerFactory.getLogger(UserService.class);


	public Collection<UserDto> getUsers() {
		return userStorage.getUsers()
				.stream()
				.map(UserMapper::mapToUserDto)
				.collect(Collectors.toList());
	}

	public Collection<UserDto> getFriends(Long id) {
		return userStorage.getFriends(id)
				.stream()
				.map(UserMapper::mapToUserDto)
				.collect(Collectors.toList());
	}

	public Collection<UserDto> getMutualFriends(Long id, Long otherId) {
		return userStorage.getMutualFriends(id, otherId)
				.stream()
				.map(UserMapper::mapToUserDto)
				.collect(Collectors.toList());
	}

	public UserDto addUser(User user) {
		log.info("Request: {}", user);
		// проверяем выполнение необходимых условий
		validateUser(user);
		user = userStorage.addUser(user);
		log.info("UserId: {}", user.getId());
		return UserMapper.mapToUserDto(user);
	}

	public UserDto changeUser(User newUser) {
		log.info("Request: {}", newUser);
		validateUser(newUser);
		newUser = userStorage.changeUser(newUser);
		log.info("Update user: {}", newUser);
		return UserMapper.mapToUserDto(newUser);
	}

	public void removeUser(Long id) {
		userStorage.removeUser(id);
	}

	public void addFriend(Long userId, Long friendId) {
		userStorage.addFriend(userId, friendId);
	}

	public void deleteFriend(Long userId, Long friendId) {
		userStorage.deleteFriend(userId, friendId);
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
		if (user.getEmail().isEmpty()) {
			log.warn("User email is not correct");
			throw new ValidationException("email не может быть пустым и содержать пробелы");
		}
	}
}

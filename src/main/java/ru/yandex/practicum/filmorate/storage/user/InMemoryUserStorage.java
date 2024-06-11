package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
	private final Map<Long, User> users = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

	@Override
	public Collection<User> getUsers() {
		return users.values();
	}

	public User getUser(Long id) {
		if (!users.containsKey(id)) {
			throw new NotFoundException("Пользователь с id = " + id + " не найден");
		}
		return users.get(id);
	}

	@Override
	public Collection<User> getFriends(Long id) {
		getUser(id);
		return users.get(id).getFriends().stream()
				.map(users::get)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<User> getMutualFriends(Long id, Long otherId) {
		getUser(id);
		getUser(otherId);

		Set<Long> otherFriendsId = users.get(otherId).getFriends();

		return users.get(id).getFriends().stream()
				.filter(otherFriendsId::contains)
				.map(users::get)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public User addUser(User user) {
		user.setId(getNextId());
		if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
		// сохраняем новую публикацию в памяти приложения
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public void removeUser(Long id) {
		if (users.containsKey(id)) users.remove(id);
		else {
			throw new NotFoundException("Пользователь с id = " + id + " не найден");
		}
	}

	@Override
	public User changeUser(User newUser) {
		// проверяем необходимые условия
		if (newUser.getId() == null) {
			throw new ValidationException("Id должен быть указан");
		}
		if (users.containsKey(newUser.getId())) {
			User oldUser = users.get(newUser.getId());
			// если публикация найдена и все условия соблюдены, обновляем её содержимое
			if (newUser.getName() != null) oldUser.setName(newUser.getName());
			if (newUser.getBirthday() != null) oldUser.setBirthday(newUser.getBirthday());
			oldUser.setEmail(newUser.getEmail());
			oldUser.setLogin(newUser.getLogin());
			return oldUser;
		} else {
			throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
		}
	}

	@Override
	public void addFriend(Long userId, Long friendId) {
		if (users.containsKey(userId) && users.containsKey(friendId)) {
			User user = users.get(userId);
			User friend = users.get(friendId);
			user.getFriends().add(friendId);
			friend.getFriends().add(userId);
		} else {
			throw new NotFoundException("Пользователь не найден");
		}
	}

	@Override
	public void deleteFriend(Long userId, Long friendId) {
		if (users.containsKey(userId) && users.containsKey(friendId)) {
			User user = users.get(userId);
			User friend = users.get(friendId);
			user.getFriends().remove(friendId);
			friend.getFriends().remove(userId);
		} else {
			throw new NotFoundException("Пользователь не найден");
		}
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
}

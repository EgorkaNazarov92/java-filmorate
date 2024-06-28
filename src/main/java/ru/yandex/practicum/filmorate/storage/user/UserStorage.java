package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
	User getUser(Long userId);

	Collection<User> getUsers();

	Collection<User> getFriends(Long id);

	Collection<User> getMutualFriends(Long id, Long otherId);

	User addUser(User user);

	void removeUser(Long id);

	User changeUser(User user);

	void addFriend(Long userId, Long friendId);

	void deleteFriend(Long userId, Long friendId);
}

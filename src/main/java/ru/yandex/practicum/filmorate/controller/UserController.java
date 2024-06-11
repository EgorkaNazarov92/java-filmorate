package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping
	public Collection<User> getUsers() {
		return userService.getUsers();
	}

	@GetMapping("/{id}/friends")
	public Collection<User> getFriends(@PathVariable("id") Long userId) {
		return userService.getFriends(userId);
	}

	@GetMapping("/{id}/friends/common/{otherId}")
	public Collection<User> getMutualFriends(@PathVariable("id") Long userId, @PathVariable("otherId") Long otherId) {
		return userService.getMutualFriends(userId, otherId);
	}

	@PostMapping
	public User addUser(@Valid @RequestBody User user) {
		return userService.addUser(user);
	}

	@PutMapping
	public User changeUser(@Valid @RequestBody User newUser) {
		return userService.changeUser(newUser);
	}

	@PutMapping("/{id}/friends/{friendId}")
	public void addFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
		userService.addFriend(userId, friendId);
	}

	@DeleteMapping("/{id}/friends/{friendId}")
	public void deleteFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
		userService.deleteFriend(userId, friendId);
	}
}

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.Film;
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
	public Collection<UserDto> getUsers() {
		return userService.getUsers();
	}

	@GetMapping("/{id}")
	public User getUser(@PathVariable("id") Long userId) {
		return userService.getUser(userId);
	}

	@GetMapping("/{id}/friends")
	public Collection<UserDto> getFriends(@PathVariable("id") Long userId) {
		return userService.getFriends(userId);
	}

	@GetMapping("/{id}/friends/common/{otherId}")
	public Collection<UserDto> getMutualFriends(@PathVariable("id") Long userId, @PathVariable("otherId") Long otherId) {
		return userService.getMutualFriends(userId, otherId);
	}

	@PostMapping
	public UserDto addUser(@Valid @RequestBody User user) {
		return userService.addUser(user);
	}

	@PutMapping
	public UserDto changeUser(@Valid @RequestBody User user) {
		return userService.changeUser(user);
	}

	@GetMapping("/{id}/recommendations")
	public Collection<Film> getRecommendations(@PathVariable("id") Long userId) {
		return userService.getRecommendationFilms(userId);
	}

	@PutMapping("/{id}/friends/{friendId}")
	public void addFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
		userService.addFriend(userId, friendId);
	}

	@DeleteMapping("/{id}/friends/{friendId}")
	public void deleteFriend(@PathVariable("id") Long userId, @PathVariable("friendId") Long friendId) {
		userService.deleteFriend(userId, friendId);
	}

	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable("id") Long userId) {
		userService.deleteUser(userId);
	}
}

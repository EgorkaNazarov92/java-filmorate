package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    private final EventStorage eventStorage;

    private FilmStorage filmStorage;

    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       EventStorage eventStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
        this.filmStorage = filmStorage;
    }

    private static final Logger log = LoggerFactory.getLogger(UserService.class);


    public Collection<UserDto> getUsers() {
        return userStorage.getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public User getUser(Long userId) {
        return userStorage.getUser(userId);
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

    public Collection<Film> getRecommendationFilms(Long userId) {
        Collection<Film> recFilms =  filmStorage.getRecommendedFilms(userId);
        log.info("{} recommended films for user with ID {}", recFilms.size(), userId);
        return recFilms;
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

    public Collection<Event> getEventsByUserId(Long userId) {
        return eventStorage.getEventsByUserId(userId);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
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

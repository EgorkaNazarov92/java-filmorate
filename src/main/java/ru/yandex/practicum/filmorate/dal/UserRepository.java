package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
	private static final String FIND_ALL_QUERY = "SELECT u.*, f.FRIEND_USER_ID FROM USERS u LEFT JOIN FRIENDS f" +
			" ON f.USER_ID = u.USER_ID";
	private static final String FIND_QUERY = "SELECT u.*, f.FRIEND_USER_ID FROM USERS u LEFT JOIN FRIENDS f" +
			" ON f.USER_ID = u.USER_ID WHERE u.USER_ID = ?";
	private static final String INSERT_QUERY = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
	private static final String UPDATE_QUERY =
			"UPDATE USERS SET NAME = ?, BIRTHDAY = ?, EMAIL = ?, LOGIN = ? WHERE USER_ID = ?";
	private static final String DELETE_QUERY = "DELETE FROM users WHERE USER_ID = ?";

	private static final String EXIST_FRIEND_QUERY =
			"SELECT * FROM FRIENDS WHERE USER_ID = ? AND FRIEND_USER_ID = ?";
	private static final String INSERT_FRIEND_QUERY = "INSERT INTO FRIENDS (USER_ID, FRIEND_USER_ID, CONFIRMED)" +
			"VALUES (?, ?, ?)";
	private static final String UPDATE_FRIEND_QUERY =
			"UPDATE FRIENDS SET CONFIRMED = ? WHERE USER_ID = ? AND FRIEND_USER_ID = ?";
	private static final String DELETE_FRIEND_QUERY = "DELETE FROM FRIENDS WHERE USER_ID = ? AND FRIEND_USER_ID = ?";
	private static final String FIND_FRIENDS_QUERY = "SELECT u.*, f.FRIEND_USER_ID FROM USERS u LEFT JOIN FRIENDS f" +
			" ON u.USER_ID = f.USER_ID WHERE u.USER_ID IN (SELECT FRIEND_USER_ID FROM FRIENDS WHERE USER_ID = ?)";
	private static final String FIND_MUTUAL_FRIENDS_QUERY = "SELECT u.*, f.FRIEND_USER_ID " +
			"FROM USERS u LEFT JOIN FRIENDS f  ON u.USER_ID = f.USER_ID " +
			"WHERE u.USER_ID IN (SELECT FRIEND_USER_ID FROM FRIENDS WHERE USER_ID = ? " +
			"AND FRIEND_USER_ID IN (SELECT FRIEND_USER_ID FROM FRIENDS WHERE USER_ID = ?))";

	public UserRepository(JdbcTemplate jdbc, ResultSetExtractor<List<User>> extractor) {
		super(jdbc, extractor);
	}

	public List<User> findAll() {
		return findMany(FIND_ALL_QUERY);
	}

	public User save(User user) {
		long id = insert(
				INSERT_QUERY,
				user.getEmail(),
				user.getLogin(),
				user.getName(),
				user.getBirthday() == null ? null : Date.valueOf(user.getBirthday())
		);
		user.setId(id);
		return user;
	}

	public User update(User user) {
		getUser(user.getId());
		update(
				UPDATE_QUERY,
				user.getName(),
				user.getBirthday(),
				user.getEmail(),
				user.getLogin(),
				user.getId()
		);
		return user;
	}

	public void delete(Long id) {
		delete(DELETE_QUERY, id);
	}


	public Optional<User> getUser(Long id) {
		return findOne(FIND_QUERY, id);
	}

	public void addFriend(Long userId, Long friendId, Boolean confirmed) {
		simpleInsert(INSERT_FRIEND_QUERY, userId, friendId, confirmed);
	}

	public Boolean existFriend(Long userId, Long friendId) {
		return exist(EXIST_FRIEND_QUERY, userId, friendId);
	}

	public void updateFriend(Long userId, Long friendId, Boolean confirmed) {
		update(UPDATE_FRIEND_QUERY, confirmed, userId, friendId);
	}

	public void deleteFriend(Long userId, Long friendId) {
		delete(DELETE_FRIEND_QUERY, userId, friendId);
	}

	public List<User> getFriends(Long id) {
		return findMany(FIND_FRIENDS_QUERY, id);
	}

	public List<User> getMutualFriends(Long id, Long friendId) {
		return findMany(FIND_MUTUAL_FRIENDS_QUERY, id, friendId);
	}
}

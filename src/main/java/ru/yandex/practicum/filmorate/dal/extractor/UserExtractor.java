package ru.yandex.practicum.filmorate.dal.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class UserExtractor implements ResultSetExtractor<List<User>> {

	@Override
	public List<User> extractData(ResultSet rs)
			throws SQLException, DataAccessException {
		Map<Long, User> userMap = new HashMap<>();

		//итерируемся по всем строкам ResultSet
		while (rs.next()) {
			Long userId = rs.getLong("USER_ID");
			if (!userMap.containsKey(userId)) {
				User user = new User(rs.getString("EMAIL"), rs.getString("LOGIN"));
				user.setId(userId);
				user.setName(rs.getString("NAME"));
				if (rs.getDate("BIRTHDAY") != null)
					user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
				Long friendId = rs.getLong("FRIEND_USER_ID");
				if (friendId != 0) {
					Set<Long> friends = user.getFriends();
					friends.add(friendId);
					user.setFriends(friends);
				}
				userMap.put(userId, user);
			} else {
				Long friendId = rs.getLong("FRIEND_USER_ID");
				if (friendId != 0) {
						User user = userMap.get(userId);
						Set<Long> friends = user.getFriends();
						friends.add(friendId);
						userMap.put(userId, user);
				}
			}
		}

		// Преобразуем Map в список
		return new ArrayList<>(userMap.values());
	}
}
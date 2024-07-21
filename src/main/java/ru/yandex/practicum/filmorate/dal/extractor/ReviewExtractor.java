package ru.yandex.practicum.filmorate.dal.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class ReviewExtractor implements ResultSetExtractor<List<Review>> {

	@Override
	public List<Review> extractData(ResultSet rs)
			throws SQLException, DataAccessException {
		Map<Long, Review> reviewMap = new HashMap<>();

		//итерируемся по всем строкам ResultSet
		while (rs.next()) {
			Long reviewId = rs.getLong("REVIEW_ID");
			if (!reviewMap.containsKey(reviewId)) {
				Review review = new Review();
				review.setReviewId(reviewId);
				review.setContent(rs.getString("CONTENT"));
				review.setIsPositive(rs.getBoolean("POSITIVE"));
				review.setUserId(rs.getLong("USER_ID"));
				review.setFilmId(rs.getLong("FILM_ID"));
				review.setUseful(rs.getInt("USEFUL"));

				reviewMap.put(reviewId, review);
			}
		}

		// Преобразуем Map в список
		return new ArrayList<>(reviewMap.values());
	}
}

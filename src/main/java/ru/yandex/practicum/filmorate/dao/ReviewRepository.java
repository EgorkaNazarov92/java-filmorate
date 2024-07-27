package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {

	public ReviewRepository(JdbcTemplate jdbc, ResultSetExtractor<List<Review>> extractor) {
		super(jdbc, extractor);
	}

	private static final String FIND_ALL_QUERY = "SELECT r.REVIEW_ID, r.CONTENT, r.POSITIVE, r.USER_ID, r.FILM_ID, " +
			"(COALESCE(l.LIKES, 0) - COALESCE(d.DISLIKES, 0)) AS USEFUL  FROM REVIEWS r " +
			"LEFT JOIN (SELECT REVIEW_ID, COUNT(USER_ID) AS LIKES FROM REVIEW_LIKES GROUP BY REVIEW_ID) l " +
			"on l.REVIEW_ID = r.REVIEW_ID " +
			"LEFT JOIN (SELECT REVIEW_ID, COUNT(USER_ID) AS DISLIKES FROM REVIEW_DISLIKES GROUP BY REVIEW_ID) d " +
			"on d.REVIEW_ID = r.REVIEW_ID";

	private static final String FIND_FILM_REVIEW_QUERY = "SELECT r.REVIEW_ID, r.CONTENT, r.POSITIVE, r.USER_ID, r.FILM_ID, " +
			"(COALESCE(l.LIKES, 0) - COALESCE(d.DISLIKES, 0)) AS USEFUL  FROM REVIEWS r " +
			"LEFT JOIN (SELECT REVIEW_ID, COUNT(USER_ID) AS LIKES FROM REVIEW_LIKES GROUP BY REVIEW_ID) l " +
			"on l.REVIEW_ID = r.REVIEW_ID " +
			"LEFT JOIN (SELECT REVIEW_ID, COUNT(USER_ID) AS DISLIKES FROM REVIEW_DISLIKES GROUP BY REVIEW_ID) d " +
			"on d.REVIEW_ID = r.REVIEW_ID " +
			"WHERE r.FILM_ID = ?";

	private static final String FIND_QUERY = "SELECT r.REVIEW_ID, r.CONTENT, r.POSITIVE, r.USER_ID, r.FILM_ID, " +
			"(COALESCE(l.LIKES, 0) - COALESCE(d.DISLIKES, 0)) AS USEFUL  FROM REVIEWS r " +
			"LEFT JOIN (SELECT REVIEW_ID, COUNT(USER_ID) AS LIKES FROM REVIEW_LIKES GROUP BY REVIEW_ID) l " +
			"on l.REVIEW_ID = r.REVIEW_ID " +
			"LEFT JOIN (SELECT REVIEW_ID, COUNT(USER_ID) AS DISLIKES FROM REVIEW_DISLIKES GROUP BY REVIEW_ID) d " +
			"on d.REVIEW_ID = r.REVIEW_ID " +
			"WHERE r.REVIEW_ID = ?";

	private static final String INSERT_QUERY = "INSERT INTO REVIEWS(CONTENT, POSITIVE, USER_ID, FILM_ID) " +
			"VALUES (?, ?, ?, ?)";

	private static final String UPDATE_QUERY =
			"UPDATE REVIEWS SET CONTENT = ?, POSITIVE = ? WHERE REVIEW_ID = ?";

	private static final String DELETE_QUERY = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";

	private static final String ADD_LIKE_QUERY = "INSERT INTO REVIEW_LIKES(REVIEW_ID, USER_ID) " +
			"VALUES (?, ?)";

	private static final String ADD_DISLIKE_QUERY = "INSERT INTO REVIEW_DISLIKES(REVIEW_ID, USER_ID) " +
			"VALUES (?, ?)";

	private static final String DELETE_LIKE_QUERY = "DELETE FROM REVIEW_LIKES " +
			"WHERE REVIEW_ID = ? AND USER_ID = ?";

	private static final String DELETE_DISLIKE_QUERY = "DELETE FROM REVIEW_DISLIKES " +
			"WHERE REVIEW_ID = ? AND USER_ID = ?";


	public List<Review> getReviews() {
		return findMany(FIND_ALL_QUERY);
	}

	public List<Review> getReviews(Long filmId) {
		return findMany(FIND_FILM_REVIEW_QUERY, filmId);
	}

	public Optional<Review> getReview(Long id) {
		return findOne(FIND_QUERY, id);
	}


	public Review addReview(Review review) {
		long id = insert(
				INSERT_QUERY,
				review.getContent(),
				review.getIsPositive(),
				review.getUserId(),
				review.getFilmId()
		);
		review.setReviewId(id);
		return review;
	}

	public Review changeReview(Review review) {
		update(
				UPDATE_QUERY,
				review.getContent(),
				review.getIsPositive(),
				review.getReviewId()
		);
		return review;
	}

	public void removeReview(Long id) {
		delete(DELETE_QUERY, id);
	}


	public void addLike(Long id, Long userId) {
		simpleInsert(ADD_LIKE_QUERY, id, userId);
	}

	public void addDislike(Long id, Long userId) {
		simpleInsert(ADD_DISLIKE_QUERY, id, userId);
	}

	public void removeLike(Long id, Long userId) {
		delete(DELETE_LIKE_QUERY, id, userId);
	}

	public void removeDislike(Long id, Long userId) {
		delete(DELETE_DISLIKE_QUERY, id, userId);
	}
}

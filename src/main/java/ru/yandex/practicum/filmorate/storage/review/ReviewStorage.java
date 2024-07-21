package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {
	Review getReview(Long id);

	Collection<Review> getReviews(Long filmId);

	Review addReview(Review review);

	Review updateReview(Review review);

	void addLike(Long id, Long userId);

	void addDislike(Long id, Long userId);

	void deleteReview(Long id);

	void deleteLike(Long id, Long userId);

	void deleteDislike(Long id, Long userId);
}

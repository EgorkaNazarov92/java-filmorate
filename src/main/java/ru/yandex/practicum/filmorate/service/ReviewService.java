package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
public class ReviewService {
	@Autowired
	@Qualifier("FilmDbStorage")
	private FilmStorage filmStorage;

	@Autowired
	@Qualifier("UserDbStorage")
	private UserStorage userStorage;

	@Autowired
	@Qualifier("ReviewDbStorage")
	private ReviewStorage reviewStorage;

	public Review getReview(Long id) {
		return reviewStorage.getReview(id);
	}

	public Collection<Review> getReviews(Long filmId, int count) {
		if (filmId != null) {
			filmStorage.getFilm(filmId);
		}
		Collection<Review> reviews = reviewStorage.getReviews(filmId);
		if (reviews.size() < count) count = reviews.size();
		return reviews.stream()
				.sorted(Comparator.comparing(Review::getUseful, Comparator.reverseOrder()))
				.toList().subList(0, count);
	}

	public Review addReview(Review review) {
		validateReview(review);
		filmStorage.getFilm(review.getFilmId());
		userStorage.getUser(review.getUserId());
		return reviewStorage.addReview(review);
	}

	public Review updateReview(Review review) {
		validateReview(review);
		filmStorage.getFilm(review.getFilmId());
		userStorage.getUser(review.getUserId());
		return reviewStorage.updateReview(review);
	}

	public void addLike(Long id, Long userId) {
		userStorage.getUser(userId);
		reviewStorage.deleteDislike(id, userId);
		reviewStorage.addLike(id, userId);
	}

	public void addDislike(Long id, Long userId) {
		userStorage.getUser(userId);
		reviewStorage.deleteLike(id, userId);
		reviewStorage.addDislike(id, userId);
	}

	public void deleteReview(Long id) {
		reviewStorage.deleteReview(id);
	}

	public void deleteLike(Long id, Long userId) {
		userStorage.getUser(userId);
		reviewStorage.deleteLike(id, userId);
	}

	public void deleteDislike(Long id, Long userId) {
		userStorage.getUser(userId);
		reviewStorage.deleteDislike(id, userId);
	}


	private void validateReview(Review review) {
		if (review.getContent() == null) {
			throw new ValidationException("Content не может быть пустым");
		}
		if (review.getUserId() == null) {
			throw new ValidationException("UserId не может быть пустым");
		}
		if (review.getFilmId() == null) {
			throw new ValidationException("FilmId не может быть пустым");
		}
		if (review.getIsPositive() == null) {
			throw new ValidationException("isPositive не может быть пустым");
		}
	}
}

package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;

@Service
public class ReviewService {
	private final FilmStorage filmStorage;

	private final UserStorage userStorage;

	private final ReviewStorage reviewStorage;

    private final EventStorage eventStorage;

    public ReviewService(
            @Qualifier("FilmDbStorage") FilmStorage filmStorage,
            @Qualifier("UserDbStorage") UserStorage userStorage,
            @Qualifier("ReviewDbStorage") ReviewStorage reviewStorage,
            EventStorage eventStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewStorage = reviewStorage;
        this.eventStorage = eventStorage;
    }

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
        User user = userStorage.getUser(review.getUserId());
        Review result = reviewStorage.addReview(review);
        Event event = Event.builder()
                .userId(user.getId())
                .entityId(result.getReviewId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.ADD)
                .build();
        eventStorage.addEvent(event);
        return result;
	}

	public Review updateReview(Review review) {
		validateReview(review);
		filmStorage.getFilm(review.getFilmId());
        userStorage.getUser(review.getUserId());
        reviewStorage.updateReview(review);
        Review updated = getReview(review.getReviewId());
        Event event = Event.builder()
                .userId(updated.getUserId())
                .entityId(updated.getReviewId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.UPDATE)
                .build();
        eventStorage.addEvent(event);
        return updated;
    }

	public void addLike(Long id, Long userId) {
        getReview(id);
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
        Review review = getReview(id);
        User user = userStorage.getUser(review.getUserId());
        reviewStorage.deleteReview(id);
        Event event = Event.builder()
                .userId(user.getId())
                .entityId(review.getReviewId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.REMOVE)
                .build();
        eventStorage.addEvent(event);
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

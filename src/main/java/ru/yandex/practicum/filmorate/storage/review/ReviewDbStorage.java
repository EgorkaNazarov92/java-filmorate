package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("ReviewDbStorage")
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
	ReviewRepository reviewRepository;

	EventRepository eventRepository;

	@Override
	public Review getReview(Long id) {
		Optional<Review> review = reviewRepository.getReview(id);
		if (review.isEmpty()) {
			throw new NotFoundException("Фильм с id = " + id + " не найден");
		}
		return review.get();
	}

	@Override
	public Collection<Review> getReviews(Long filmId) {
		if (filmId == null) return reviewRepository.getReviews();
		return reviewRepository.getReviews(filmId);
	}

	@Override
	public Review addReview(Review review) {
        return reviewRepository.addReview(review);
	}

	@Override
	public Review updateReview(Review review) {
        return reviewRepository.changeReview(review);
	}

	@Override
	public void addLike(Long id, Long userId) {
		getReview(id);
		reviewRepository.addLike(id, userId);
	}

	@Override
	public void addDislike(Long id, Long userId) {
		getReview(id);
		reviewRepository.addDislike(id, userId);
	}

	@Override
	public void deleteReview(Long id) {
		getReview(id);
		reviewRepository.removeReview(id);
	}

	@Override
	public void deleteLike(Long id, Long userId) {
		getReview(id);
		reviewRepository.removeLike(id, userId);
	}

	@Override
	public void deleteDislike(Long id, Long userId) {
		getReview(id);
		reviewRepository.removeDislike(id, userId);
	}
}

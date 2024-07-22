package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;

import java.time.Instant;
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
        Review result = reviewRepository.addReview(review);
        Event event = Event.builder()
                .userId(result.getUserId())
                .entityId(result.getReviewId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.ADD)
                .build();
        eventRepository.addEvent(event);
        return result;
    }

    @Override
    public Review updateReview(Review review) {
        getReview(review.getReviewId());
        Review result = reviewRepository.changeReview(review);
        Event event = Event.builder()
                .userId(result.getUserId())
                .entityId(result.getReviewId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.UPDATE)
                .build();
        eventRepository.addEvent(event);
        return result;
    }

    @Override
    public void addLike(Long id, Long userId) {
        getReview(id);
        reviewRepository.addLike(id, userId);
        Event event = Event.builder()
                .userId(userId)
                .entityId(id)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.UPDATE)
                .build();
        eventRepository.addEvent(event);
    }

    @Override
    public void addDislike(Long id, Long userId) {
        getReview(id);
        reviewRepository.addDislike(id, userId);
        Event event = Event.builder()
                .userId(userId)
                .entityId(id)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.UPDATE)
                .build();
        eventRepository.addEvent(event);
    }

    @Override
    public void deleteReview(Long id) {
        Review review = getReview(id);
        reviewRepository.removeReview(id);
        Event event = Event.builder()
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.REMOVE)
                .build();
        eventRepository.addEvent(event);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        getReview(id);
        reviewRepository.removeLike(id, userId);
        Event event = Event.builder()
                .userId(userId)
                .entityId(id)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.UPDATE)
                .build();
        eventRepository.addEvent(event);
    }

    @Override
    public void deleteDislike(Long id, Long userId) {
        getReview(id);
        reviewRepository.removeDislike(id, userId);
        Event event = Event.builder()
                .userId(userId)
                .entityId(id)
                .timestamp(Instant.now().toEpochMilli())
                .eventType(Event.EventType.REVIEW)
                .operation(Event.Operation.UPDATE)
                .build();
        eventRepository.addEvent(event);
    }
}

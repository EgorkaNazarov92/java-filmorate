package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
	private final ReviewService reviewService;

	@GetMapping("/{id}")
	public Review getReview(@PathVariable("id") Long id) {
		return reviewService.getReview(id);
	}

	@GetMapping
	public Collection<Review> getReviews(@RequestParam(required = false) Long filmId,
										 @RequestParam(defaultValue = "10") int count) {
		return reviewService.getReviews(filmId, count);
	}

	@PostMapping
	public Review addReview(@RequestBody Review review) {
		return reviewService.addReview(review);
	}

	@PutMapping
	public Review updateReview(@RequestBody Review review) {
		return reviewService.updateReview(review);
	}

	@PutMapping("/{id}/like/{userId}")
	public void addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
		reviewService.addLike(id, userId);
	}

	@PutMapping("/{id}/dislike/{userId}")
	public void addDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
		reviewService.addDislike(id, userId);
	}

	@DeleteMapping({"/{id}"})
	public void deleteReview(@PathVariable("id") Long id) {
		reviewService.deleteReview(id);
	}

	@DeleteMapping({"/{id}/like/{userId}"})
	public void deleteLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
		reviewService.deleteLike(id, userId);
	}

	@DeleteMapping({"/{id}/dislike/{userId}"})
	public void deleteDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
		reviewService.deleteDislike(id, userId);
	}
}

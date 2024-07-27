package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
	private static final String FIND_MPA_QUERY = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
	private static final String FIND_ALL_QUERY = "SELECT * FROM GENRES";

	public GenreRepository(JdbcTemplate jdbc, ResultSetExtractor<List<Genre>> extractor) {
		super(jdbc, extractor);
	}


	public Optional<Genre> getGenre(Integer id) {
		return findOne(FIND_MPA_QUERY, id);
	}

	public List<Genre> getGenres() {
		return findMany(FIND_ALL_QUERY);
	}
}
package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {
	private static final String FIND_MPA_QUERY = "SELECT * FROM MPA WHERE MPA_ID = ?";
	private static final String FIND_ALL_QUERY = "SELECT * FROM MPA";

	public MpaRepository(JdbcTemplate jdbc, ResultSetExtractor<List<Mpa>> extractor) {
		super(jdbc, extractor);
	}


	public Optional<Mpa> getMpa(Integer id) {
		return findOne(FIND_MPA_QUERY, id);
	}

	public List<Mpa> getMpas() {
		return findMany(FIND_ALL_QUERY);
	}
}

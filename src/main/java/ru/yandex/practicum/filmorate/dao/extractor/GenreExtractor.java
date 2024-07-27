package ru.yandex.practicum.filmorate.dao.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenreExtractor implements ResultSetExtractor<List<Genre>> {

	@Override
	public List<Genre> extractData(ResultSet rs)
			throws SQLException, DataAccessException {
		List<Genre> genres = new ArrayList<>();

		//итерируемся по всем строкам ResultSet
		while (rs.next()) {
			Genre genre = new Genre();
			genre.setId(rs.getInt("GENRE_ID"));
			genre.setName(rs.getString("NAME"));

			genres.add(genre);
		}

		return genres;
	}
}

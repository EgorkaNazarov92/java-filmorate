package ru.yandex.practicum.filmorate.dal.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DirectorExtractor implements ResultSetExtractor<List<Director>> {

	@Override
	public List<Director> extractData(ResultSet rs)
			throws SQLException, DataAccessException {
		List<Director> directors = new ArrayList<>();

		//итерируемся по всем строкам ResultSet
		while (rs.next()) {
			Director director = new Director();
			director.setId(rs.getLong("DIRECTOR_ID"));
			director.setName(rs.getString("NAME"));

			directors.add(director);
		}

		return directors;
	}

}

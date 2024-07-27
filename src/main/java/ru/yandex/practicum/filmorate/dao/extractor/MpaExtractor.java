package ru.yandex.practicum.filmorate.dao.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MpaExtractor implements ResultSetExtractor<List<Mpa>> {

	@Override
	public List<Mpa> extractData(ResultSet rs)
			throws SQLException, DataAccessException {
		List<Mpa> mpas = new ArrayList<>();

		//итерируемся по всем строкам ResultSet
		while (rs.next()) {
			Mpa mpa = new Mpa();
			mpa.setId(rs.getInt("MPA_ID"));
			mpa.setName(rs.getString("NAME"));

			mpas.add(mpa);
		}

		return mpas;
	}
}

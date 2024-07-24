package ru.yandex.practicum.filmorate.dal.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmExtractor implements ResultSetExtractor<List<Film>> {

	@Override
	public List<Film> extractData(ResultSet rs)
			throws SQLException, DataAccessException {
		Map<Long, Film> filmMap = new HashMap<>();

		//итерируемся по всем строкам ResultSet
		while (rs.next()) {
			Long filmId = rs.getLong("FILM_ID");
			if (!filmMap.containsKey(filmId)) {
				Film film = new Film();
				film.setId(filmId);
				film.setName(rs.getString("NAME"));
				film.setDescription(rs.getString("DESCRIPTION"));
				film.setReleaseDate(rs.getDate("RELEASE_DATE").toLocalDate());
				film.setDuration(rs.getInt("DURATION"));

				int mpaId = rs.getInt("MPA_ID");
				if (mpaId != 0) {
					Mpa mpa = new Mpa();
					mpa.setId(rs.getInt("MPA_ID"));
					mpa.setName(rs.getString("MPA_NAME"));
					film.setMpa(mpa);
				}

				int genreId = rs.getInt("GENRE_ID");
				if (genreId != 0) {
					Genre genre = new Genre();
					genre.setId(genreId);
					genre.setName(rs.getString("GENRE_NAME"));
					Set<Genre> genres = film.getGenres();
					genres.add(genre);

					film.setGenres(genres);
				}

				Long like = rs.getLong("USER_ID");
				if (like != 0) film.getLikes().add(like);

				int directorId = rs.getInt("DIRECTOR_ID");
				if (directorId != 0) {
					Director director = new Director();
					director.setId(directorId);
					director.setName(rs.getString("DIRECTOR_NAME"));
					Set<Director> directors = film.getDirector();
					directors.add(director);

					film.setDirector(directors);
				}

				filmMap.put(filmId, film);

			} else {
				Film film = filmMap.get(filmId);

				int genreId = rs.getInt("GENRE_ID");
				if (genreId != 0) {
					Genre genre = new Genre();
					genre.setId(rs.getInt("GENRE_ID"));
					genre.setName(rs.getString("GENRE_NAME"));
					Set<Genre> genres = film.getGenres();
					genres.add(genre);

					film.setGenres(genres);
				}

				Long like = rs.getLong("USER_ID");
				if (like != 0) film.getLikes().add(like);

				int directorId = rs.getInt("DIRECTOR_ID");
				if (directorId != 0) {
					Director director = new Director();
					director.setId(directorId);
					director.setName(rs.getString("DIRECTOR_NAME"));
					Set<Director> directors = film.getDirector();
					directors.add(director);

					film.setDirector(directors);
				}

				filmMap.put(filmId, film);
			}
		}

		// Преобразуем Map в список
		return new ArrayList<>(filmMap.values());
	}
}
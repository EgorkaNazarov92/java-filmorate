package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {
    private static final String FIND_DIRECTOR_QUERY = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM DIRECTORS";
    private static final String INSERT_QUERY = "INSERT INTO DIRECTORS (DIRECTOR_ID, NAME) VALUES(?, ?)";
    private static final String UPDATE_QUERY = "UPDATE DIRECTORS SET NAME = ? WHERE DIRECTOR_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";

    public DirectorRepository(JdbcTemplate jdbc, ResultSetExtractor<List<Director>> extractor) {
        super(jdbc, extractor);
    }


    public Optional<Director> getDirector(Integer id) {
        return findOne(FIND_DIRECTOR_QUERY, id);
    }

    public List<Director> getDirectors() {
        return findMany(FIND_ALL_QUERY);
    }

    public Director addDirector(Director director) {
        simpleInsert(INSERT_QUERY, director.getId(), director.getName());
        return director;
    }

    public Director updateDirector(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        return director;
    }

    public void deleteDirector(Integer id) {
        delete(DELETE_QUERY, id);
    }
}

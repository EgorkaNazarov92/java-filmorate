package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
public class EventRepository extends BaseRepository<Event> {
    public EventRepository(JdbcTemplate jdbc, ResultSetExtractor<List<Event>> extractor) {
        super(jdbc, extractor);
    }

    private static final String GET_EVENTS_BY_USER_ID = "SELECT * FROM EVENTS WHERE USER_ID = ?";

    private static final String INSERT = "INSERT INTO EVENTS (USER_ID, ENTITY_ID, TIMESTAMP, EVENT_TYPE, OPERATION) VALUES(?, ?, ?, ?, ?)";

    public List<Event> getEventsByUserId(final long userId) {
        return findMany(GET_EVENTS_BY_USER_ID, userId);
    }

    public void addEvent(final Event event) {
        insert(
                INSERT,
                event.getUserId(),
                event.getEntityId(),
                event.getTimestamp(),
                event.getEventType().toString(),
                event.getOperation().toString()
        );
    }
}

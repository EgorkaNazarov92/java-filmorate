package ru.yandex.practicum.filmorate.dao.extractor;

import lombok.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventExtractor implements ResultSetExtractor<List<Event>> {
    @Override
    public List<Event> extractData(@NonNull ResultSet rs) throws DataAccessException, SQLException {
        List<Event> events = new ArrayList<>();

        while (rs.next()) {
            Event event = Event.builder()
                    .eventId(rs.getLong("EVENT_ID"))
                    .userId(rs.getLong("USER_ID"))
                    .entityId(rs.getLong("ENTITY_ID"))
                    .timestamp(rs.getLong("TIMESTAMP"))
                    .eventType(Event.EventType.valueOf(rs.getString("EVENT_TYPE")))
                    .operation(Event.Operation.valueOf(rs.getString("OPERATION")))
                    .build();

            events.add(event);
        }

        return events;
    }
}

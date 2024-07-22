package ru.yandex.practicum.filmorate.storage.event;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.EventRepository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

@Component
@Primary
@AllArgsConstructor
public class EventStorageImpl implements EventStorage {
    private final EventRepository eventRepository;

    @Override
    public Collection<Event> getEventsByUserId(long userId) {
        return eventRepository.getEventsByUserId(userId);
    }

    @Override
    public void addEvent(Event event) {
        eventRepository.addEvent(event);
    }
}

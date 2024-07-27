package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private Long eventId;
    private Long userId;
    private Long entityId;
    private Long timestamp;
    private EventType eventType;
    private Operation operation;

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum Operation {
        REMOVE,
        ADD,
        UPDATE
    }
}

package de.example.data.OCPI;

import lombok.Getter;

@Getter
public enum Status {
    AVAILABLE,
    BLOCKED,
    CHARGING,
    INOPERATIVE,
    OUTOFORDER,
    PLANNED,
    REMOVED,
    REVERSED,
    UNKNOWN
}

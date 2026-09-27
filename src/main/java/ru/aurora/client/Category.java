package ru.aurora.client;

public enum Category {
    COMBAT("Бой"),
    MOVEMENT("Движение"),
    VISUAL("Визуал"),
    PLAYER("Игрок"),
    UTILITY("Утилиты");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}

package ru.aurora.client.modules;

public enum Category {
    COMBAT("Бой", "⚔"),
    MOVEMENT("Движение", "↗"),
    RENDER("Визуал", "◈"),
    PLAYER("Игрок", "♙"),
    MISC("Утилиты", "⚙");

    public final String displayName;
    public final String icon;

    Category(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }
}

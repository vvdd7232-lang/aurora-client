package ru.aurora.client;

public abstract class Module {
    private final String id;
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;

    protected Module(String id, String name, String description, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Category category() {
        return category;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        if (setEnabledInternal(enabled)) {
            ModuleManager manager = AuroraClient.modules();
            if (manager != null) {
                manager.save();
            }
        }
    }

    public final void toggle() {
        setEnabled(!enabled);
    }

    /** Applies the state without persisting; returns true if the state changed. */
    final boolean setEnabledInternal(boolean enabled) {
        if (this.enabled == enabled) {
            return false;
        }
        this.enabled = enabled;
        try {
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        } catch (Throwable t) {
            AuroraClient.LOGGER.warn("Module {} failed to {}", id, enabled ? "enable" : "disable", t);
        }
        return true;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }
}

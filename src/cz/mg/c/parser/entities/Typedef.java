package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;

public @Entity class Typedef extends Typename {
    private Type type;

    public Typedef() {
    }

    @Required @Shared
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

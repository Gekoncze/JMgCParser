package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.c.parser.entities.types.Type;

public @Entity class Typedef extends Typename implements CMainEntity {
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

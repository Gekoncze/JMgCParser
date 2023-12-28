package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;

public @Entity class CTypedef extends CTypename {
    private CType type;

    public CTypedef() {
    }

    public CTypedef(@Optional String name, CType type) {
        super(name);
        this.type = type;
    }

    @Required @Shared
    public CType getType() {
        return type;
    }

    public void setType(CType type) {
        this.type = type;
    }
}

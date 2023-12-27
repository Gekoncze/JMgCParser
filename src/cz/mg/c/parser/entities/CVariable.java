package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.annotations.storage.Value;

public @Entity class CVariable implements CMainEntity {
    private CType type;
    private String name;

    public CVariable() {
    }

    @Required @Shared
    public CType getType() {
        return type;
    }

    public void setType(CType type) {
        this.type = type;
    }

    @Optional @Value
    public String getName() {
        return name;
    }

    public void setName(@Optional String name) {
        this.name = name;
    }
}

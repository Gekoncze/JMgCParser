package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.storage.Shared;

public @Entity class CTypename implements CMainEntity {
    private String name;

    public CTypename() {
    }

    public CTypename(@Optional String name) {
        this.name = name;
    }

    @Optional @Shared
    public String getName() {
        return name;
    }

    public void setName(@Optional String name) {
        this.name = name;
    }
}

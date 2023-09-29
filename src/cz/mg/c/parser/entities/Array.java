package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class Array implements CEntity {
    private int size;

    public Array() {
    }

    @Required @Value
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

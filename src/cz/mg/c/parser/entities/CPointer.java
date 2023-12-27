package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class CPointer implements CEntity {
    boolean constant;

    public CPointer() {
    }

    public CPointer(boolean constant) {
        this.constant = constant;
    }

    @Required @Value
    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }
}

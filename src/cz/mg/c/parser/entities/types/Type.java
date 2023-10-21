package cz.mg.c.parser.entities.types;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.c.parser.entities.CEntity;
import cz.mg.c.parser.entities.Pointer;
import cz.mg.collections.list.List;

public abstract @Entity class Type implements CEntity {
    private List<Pointer> pointers = new List<>();

    public Type() {
    }

    @Required @Part
    public List<Pointer> getPointers() {
        return pointers;
    }

    public void setPointers(List<Pointer> pointers) {
        this.pointers = pointers;
    }
}

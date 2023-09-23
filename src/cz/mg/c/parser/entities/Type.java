package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Link;
import cz.mg.annotations.storage.Part;
import cz.mg.collections.list.List;

public @Entity class Type {
    private Typename typename;
    private List<Pointer> pointers = new List<>();
    private List<Array> arrays = new List<>();

    public Type() {
    }

    @Required @Link
    public Typename getTypename() {
        return typename;
    }

    public void setTypename(Typename typename) {
        this.typename = typename;
    }

    @Required @Part
    public List<Pointer> getPointers() {
        return pointers;
    }

    public void setPointers(List<Pointer> pointers) {
        this.pointers = pointers;
    }

    @Required @Part
    public List<Array> getArrays() {
        return arrays;
    }

    public void setArrays(List<Array> arrays) {
        this.arrays = arrays;
    }
}

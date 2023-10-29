package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Shared;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

public @Entity class Type implements CEntity {
    private Typename typename;
    private boolean constant;
    private List<Pointer> pointers = new List<>();
    private List<Array> arrays = new List<>();

    public Type() {
    }

    @Required @Shared
    public Typename getTypename() {
        return typename;
    }

    public void setTypename(Typename typename) {
        this.typename = typename;
    }

    @Required @Value
    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
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

package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Shared;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

public @Entity class CType implements CEntity {
    private CTypename typename;
    private boolean constant;
    private List<CPointer> pointers = new List<>();
    private List<CArray> arrays = new List<>();

    public CType() {
    }

    @Required @Shared
    public CTypename getTypename() {
        return typename;
    }

    public void setTypename(CTypename typename) {
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
    public List<CPointer> getPointers() {
        return pointers;
    }

    public void setPointers(List<CPointer> pointers) {
        this.pointers = pointers;
    }

    @Required @Part
    public List<CArray> getArrays() {
        return arrays;
    }

    public void setArrays(List<CArray> arrays) {
        this.arrays = arrays;
    }
}

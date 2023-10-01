package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Link;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Entity class Type implements CEntity {
    private NameToken typename;
    private boolean constant;
    private List<Pointer> pointers = new List<>();

    public Type() {
    }

    @Required @Link
    public NameToken getTypename() {
        return typename;
    }

    public void setTypename(NameToken typename) {
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
}

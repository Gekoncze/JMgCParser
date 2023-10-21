package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Shared;
import cz.mg.annotations.storage.Value;
import cz.mg.c.parser.entities.types.Type;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Entity class Variable implements CMainEntity {
    private Type type;
    private NameToken name;
    private List<Array> arrays = new List<>();

    public Variable() {
    }

    @Required @Shared
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Required @Value
    public NameToken getName() {
        return name;
    }

    public void setName(NameToken name) {
        this.name = name;
    }

    @Required @Part
    public List<Array> getArrays() {
        return arrays;
    }

    public void setArrays(List<Array> arrays) {
        this.arrays = arrays;
    }
}

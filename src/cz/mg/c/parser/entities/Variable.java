package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.annotations.storage.Value;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Entity class Variable {
    private Type type;
    private NameToken name;

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
}

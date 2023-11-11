package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.annotations.storage.Value;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Entity class Variable implements CMainEntity {
    private Type type;
    private WordToken name;

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
    public WordToken getName() {
        return name;
    }

    public void setName(WordToken name) {
        this.name = name;
    }
}

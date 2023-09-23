package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Entity class Typename {
    private NameToken name;

    public Typename() {
    }

    @Required @Value
    public NameToken getName() {
        return name;
    }

    public void setName(NameToken name) {
        this.name = name;
    }
}

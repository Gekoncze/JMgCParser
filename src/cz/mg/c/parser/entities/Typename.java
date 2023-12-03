package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Entity class Typename implements CEntity {
    private WordToken name;

    public Typename() {
    }

    public Typename(WordToken name) {
        this.name = name;
    }

    @Required @Shared
    public WordToken getName() {
        return name;
    }

    public void setName(WordToken name) {
        this.name = name;
    }
}

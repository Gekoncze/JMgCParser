package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Entity class CTypename implements CMainEntity {
    private WordToken name;

    public CTypename() {
    }

    public CTypename(WordToken name) {
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

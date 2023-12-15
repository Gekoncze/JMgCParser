package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.annotations.storage.Value;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Entity class CVariable implements CMainEntity {
    private CType type;
    private WordToken name;

    public CVariable() {
    }

    @Required @Shared
    public CType getType() {
        return type;
    }

    public void setType(CType type) {
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

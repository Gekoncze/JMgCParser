package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.storage.Shared;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class CEnumEntry implements CEntity {
    private String name;
    private List<Token> expression;

    public CEnumEntry() {
    }

    @Optional @Shared
    public String getName() {
        return name;
    }

    public void setName(@Optional String name) {
        this.name = name;
    }

    @Optional @Shared
    public List<Token> getExpression() {
        return expression;
    }

    public void setExpression(List<Token> expression) {
        this.expression = expression;
    }
}

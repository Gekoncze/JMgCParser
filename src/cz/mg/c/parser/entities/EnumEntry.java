package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Entity class EnumEntry implements CEntity {
    private NameToken name;
    private List<Token> expression;

    public EnumEntry() {
    }

    @Required @Shared
    public NameToken getName() {
        return name;
    }

    public void setName(NameToken name) {
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

package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class CArray implements CEntity {
    private List<Token> expression = new List<>();

    public CArray() {
    }

    public CArray(List<Token> expression) {
        this.expression = expression;
    }

    @Required @Shared
    public List<Token> getExpression() {
        return expression;
    }

    public void setExpression(List<Token> expression) {
        this.expression = expression;
    }
}

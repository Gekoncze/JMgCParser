package cz.mg.c.parser.entities.brackets;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class Brackets extends Token {
    private List<Token> tokens = new List<>();

    public Brackets() {
    }

    public Brackets(String text, int position, List<Token> tokens) {
        super(text, position);
        this.tokens = tokens;
    }

    @Required @Shared
    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }
}
package cz.mg.c.parser.entities.groups;

import cz.mg.annotations.classes.Entity;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class RoundBrackets extends Brackets {
    public RoundBrackets() {
    }

    public RoundBrackets(String text, int position, List<Token> tokens) {
        super(text, position, tokens);
    }
}

package cz.mg.c.parser.entities.brackets;

import cz.mg.annotations.classes.Entity;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class CurlyBrackets extends Brackets {
    public CurlyBrackets() {
    }

    public CurlyBrackets(String text, int position, List<Token> tokens) {
        super(text, position, tokens);
    }
}

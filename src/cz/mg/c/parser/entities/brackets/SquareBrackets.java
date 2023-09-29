package cz.mg.c.parser.entities.brackets;

import cz.mg.annotations.classes.Entity;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class SquareBrackets extends Brackets {
    public SquareBrackets() {
    }

    public SquareBrackets(String text, int position, List<Token> tokens) {
        super(text, position, tokens);
    }
}

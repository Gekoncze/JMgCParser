package cz.mg.c.parser.entities.groups;

import cz.mg.annotations.classes.Entity;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class Brackets extends Group {
    public Brackets() {
    }

    public Brackets(String text, int position, List<Token> tokens) {
        super(text, position, tokens);
    }
}

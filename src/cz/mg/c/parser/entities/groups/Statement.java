package cz.mg.c.parser.entities.groups;

import cz.mg.annotations.classes.Entity;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class Statement extends Group {
    public Statement() {
    }

    public Statement(String text, int position, List<Token> tokens) {
        super(text, position, tokens);
    }
}

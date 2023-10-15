package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.ReadableList;
import cz.mg.tokenizer.entities.Token;

public @Component class TokenReader extends cz.mg.tokenizer.components.TokenReader {
    public TokenReader(@Mandatory ReadableList<Token> tokens) {
        super(tokens, ParseException::new);
    }
}

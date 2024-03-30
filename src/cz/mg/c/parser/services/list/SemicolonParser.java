package cz.mg.c.parser.services.list;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.SymbolToken;

public @Service class SemicolonParser {
    private static volatile @Service SemicolonParser instance;

    public static @Service SemicolonParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new SemicolonParser();
                }
            }
        }
        return instance;
    }

    private SemicolonParser() {
    }

    public @Mandatory List<List<Token>> parse(@Mandatory List<Token> tokens) {
        List<List<Token>> groups = new List<>();
        List<Token> group = new List<>();
        TokenReader reader = new TokenReader(tokens);

        while (reader.has()) {
            if (reader.has(";", SymbolToken.class)) {
                reader.read();
                if (!group.isEmpty()) {
                    groups.addLast(group);
                }
                group = new List<>();
            } else {
                group.addLast(reader.read());
            }
        }

        if (!group.isEmpty()) {
            Token last = group.getLast();
            throw new ParseException(
                last.getPosition(),
                "Missing semicolon after '" + last.getText() + "' token."
            );
        }

        return groups;
    }
}
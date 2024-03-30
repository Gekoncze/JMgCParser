package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.SymbolToken;

public @Service class InitializerParser {
    private static volatile @Service InitializerParser instance;

    public static @Service InitializerParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new InitializerParser();
                }
            }
        }
        return instance;
    }

    private InitializerParser() {
    }

    public @Optional List<Token> parse(@Mandatory TokenReader reader) {
        if (reader.has("=", SymbolToken.class)) {
            int position = reader.read("=", SymbolToken.class).getPosition();
            List<Token> expression = new List<>();
            while (reader.has() && !reader.has(";", SymbolToken.class)) {
                expression.addLast(reader.read());
            }
            if (expression.isEmpty()) {
                throw new ParseException(position, "Missing expression.");
            }
            return expression;
        } else {
            return null;
        }
    }
}
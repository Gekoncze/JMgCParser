package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.CEnumEntry;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.services.CEntityParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.WordToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;

public @Service class EnumEntryParser implements CEntityParser {
    private static volatile @Service EnumEntryParser instance;

    public static @Service EnumEntryParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new EnumEntryParser();
                }
            }
        }
        return instance;
    }

    private EnumEntryParser() {
    }

    @Override
    public @Mandatory CEnumEntry parse(@Mandatory TokenReader reader) {
        CEnumEntry entry = new CEnumEntry();
        entry.setName(reader.read(WordToken.class));
        if (reader.has("=", OperatorToken.class)) {
            entry.setExpression(readExpression(reader));
        }
        return entry;
    }

    private @Mandatory List<Token> readExpression(TokenReader reader) {
        int position = reader.read("=", OperatorToken.class).getPosition();
        List<Token> expression = new List<>();
        while (reader.has()) {
            expression.addLast(reader.read());
        }
        if (expression.isEmpty()) {
            throw new ParseException(position, "Missing expression.");
        }
        return expression;
    }
}

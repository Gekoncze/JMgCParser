package cz.mg.c.parser.services.lists;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.lists.Statement;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.components.TokenReader;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

public @Service class StatementParser {
    private static volatile @Service StatementParser instance;

    public static @Service StatementParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StatementParser();
                }
            }
        }
        return instance;
    }

    private StatementParser() {
    }

    public @Mandatory List<Statement> parse(@Mandatory List<Token> tokens) {
        List<Statement> statements = new List<>();
        Statement statement = new Statement();
        TokenReader reader = new TokenReader(tokens, ParseException::new);

        while (reader.has()) {
            if (reader.has(";", SeparatorToken.class)) {
                reader.read();
                if (!statement.getTokens().isEmpty()) {
                    statements.addLast(statement);
                }
                statement = new Statement();
            } else {
                statement.getTokens().addLast(reader.read());
            }
        }

        if (!statement.getTokens().isEmpty()) {
            Token last = statement.getTokens().getLast();
            throw new ParseException(
                last.getPosition(),
                "Missing semicolon after '" + last.getText() + "' token."
            );
        }

        return statements;
    }
}

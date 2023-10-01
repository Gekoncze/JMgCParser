package cz.mg.c.parser.services.statement;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.c.parser.entities.lists.Statement;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.components.TokenReader;

public @Service class BlockStatementParser {
    private static volatile @Service BlockStatementParser instance;

    public static @Service BlockStatementParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new BlockStatementParser();
                }
            }
        }
        return instance;
    }

    private BlockStatementParser() {
    }

    public @Mandatory List<Statement> parse(@Mandatory List<Statement> statements) {
        List<Statement> newStatements = new List<>();
        for (Statement statement : statements) {
            newStatements.addCollectionLast(parse(statement));
        }
        newStatements.removeIf(statement -> statement.getTokens().isEmpty());
        return newStatements;
    }

    private @Mandatory List<Statement> parse(@Mandatory Statement statement) {
        List<Statement> newStatements = new List<>();
        newStatements.addLast(new Statement());
        TokenReader reader = new TokenReader(statement.getTokens(), ParseException::new);
        while (reader.has()) {
            boolean roundBrackets = reader.hasPrevious(RoundBrackets.class);
            boolean curlyBrackets = reader.has(CurlyBrackets.class);
            newStatements.getLast().getTokens().addLast(reader.read());
            if (roundBrackets && curlyBrackets) {
                newStatements.addLast(new Statement());
            }
        }
        return newStatements;
    }
}

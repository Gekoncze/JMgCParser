package cz.mg.c.parser.services.statement;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.lists.Statement;
import cz.mg.c.parser.services.bracket.CurlyBracketParser;
import cz.mg.c.parser.services.bracket.RoundBracketParser;
import cz.mg.c.parser.services.bracket.SquareBracketParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Service class StatementParser {
    private static volatile @Service StatementParser instance;

    public static @Service StatementParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StatementParser();
                    instance.roundBracketParser = RoundBracketParser.getInstance();
                    instance.squareBracketParser = SquareBracketParser.getInstance();
                    instance.curlyBracketParser = CurlyBracketParser.getInstance();
                    instance.blockStatementParser = BlockStatementParser.getInstance();
                    instance.semicolonStatementParser = SemicolonStatementParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service RoundBracketParser roundBracketParser;
    private @Service SquareBracketParser squareBracketParser;
    private @Service CurlyBracketParser curlyBracketParser;
    private @Service BlockStatementParser blockStatementParser;
    private @Service SemicolonStatementParser semicolonStatementParser;

    private StatementParser() {
    }

    public @Mandatory List<Statement> parse(@Mandatory List<Token> tokens) {
        return blockStatementParser.parse(
            parseSquareBrackets(
                parseRoundBrackets(
                    semicolonStatementParser.parse(
                        curlyBracketParser.parse(tokens)
                    )
                )
            )
        );
    }

    private @Mandatory List<Statement> parseRoundBrackets(@Mandatory List<Statement> statements) {
        for (Statement statement : statements) {
            statement.setTokens(roundBracketParser.parse(statement.getTokens()));
        }
        return statements;
    }

    private @Mandatory List<Statement> parseSquareBrackets(@Mandatory List<Statement> statements) {
        for (Statement statement : statements) {
            statement.setTokens(squareBracketParser.parse(statement.getTokens()));
        }
        return statements;
    }
}

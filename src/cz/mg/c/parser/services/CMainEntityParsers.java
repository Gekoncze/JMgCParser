package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.lists.Statement;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.services.brackets.RoundBracketParser;
import cz.mg.c.parser.services.brackets.SquareBracketParser;
import cz.mg.collections.list.List;
import cz.mg.collections.list.ReadableList;
import cz.mg.collections.pair.Pair;
import cz.mg.collections.pair.ReadablePair;
import cz.mg.tokenizer.entities.Token;

public @Service class CMainEntityParsers {
    private static volatile @Service CMainEntityParsers instance;

    public static @Service CMainEntityParsers getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new CMainEntityParsers();
                    instance.roundBracketParser = RoundBracketParser.getInstance();
                    instance.squareBracketParser = SquareBracketParser.getInstance();
                    instance.entityParsers = new List<>(); // TODO - add entity parsers
                }
            }
        }
        return instance;
    }

    private @Service RoundBracketParser roundBracketParser;
    private @Service SquareBracketParser squareBracketParser;
    private @Service List<Pair<Pattern, CMainEntityParser>> entityParsers;

    private CMainEntityParsers() {
    }

    public @Mandatory List<CMainEntity> parse(@Mandatory ReadableList<Statement> statements) {
        List<CMainEntity> entities = new List<>();
        for (Statement statement : statements) {
            if (!statement.getTokens().isEmpty()) {
                entities.addLast(parseStatement(statement));
            }
        }
        return entities;
    }

    private @Mandatory CMainEntity parseStatement(@Mandatory Statement statement) {
        List<Token> tokens = squareBracketParser.parse(
            roundBracketParser.parse(
                statement.getTokens()
            )
        );

        for (ReadablePair<Pattern, CMainEntityParser> pair : entityParsers) {
            if (pair.getKey().matches(tokens)) {
                return pair.getValue().parse(tokens);
            }
        }

        throw new ParseException(statement.getPosition(), "Could not recognize statement.");
    }
}

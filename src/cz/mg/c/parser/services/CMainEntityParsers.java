package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.Statement;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
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
                    instance.entityParsers = new List<>(); // TODO - add entity parsers
                }
            }
        }
        return instance;
    }

    private @Service List<Pair<Pattern, CMainEntityParser>> entityParsers;

    private CMainEntityParsers() {
    }

    public @Mandatory List<CMainEntity> parse(@Mandatory List<Statement> statements) {
        List<CMainEntity> entities = new List<>();
        for (Statement statement : statements) {
            if (!statement.getTokens().isEmpty()) {
                entities.addLast(parseStatement(statement));
            }
        }
        return entities;
    }

    private @Mandatory CMainEntity parseStatement(@Mandatory Statement statement) {
        CMainEntityParser parser = findParser(statement);
        TokenReader reader = new TokenReader(statement.getTokens());
        CMainEntity entity = parser.parse(reader);
        reader.readEnd();
        return entity;
    }

    private @Mandatory CMainEntityParser findParser(@Mandatory Statement statement) {
        for (ReadablePair<Pattern, CMainEntityParser> pair : entityParsers) {
            if (pair.getKey().matches(statement.getTokens())) {
                return pair.getValue();
            }
        }

        Token firstToken = statement.getTokens().getFirst();
        int position = firstToken != null ? firstToken.getPosition() : -1;
        throw new ParseException(position, "Could not recognize statement.");
    }
}

package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.lists.Statement;
import cz.mg.collections.list.List;

public @Service class CMainEntityParser {
    private static volatile @Service CMainEntityParser instance;

    public static @Service CMainEntityParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new CMainEntityParser();
                }
            }
        }
        return instance;
    }

    private CMainEntityParser() {
    }

    public @Mandatory List<CMainEntity> parse(@Mandatory List<Statement> statements) {
        List<CMainEntity> entities = new List<>();
        for (Statement statement : statements) {
            entities.addLast(parse(statement));
        }
        return entities;
    }

    private @Mandatory CMainEntity parse(@Mandatory Statement statement) {
        throw new UnsupportedOperationException("TODO"); // TODO
    }
}

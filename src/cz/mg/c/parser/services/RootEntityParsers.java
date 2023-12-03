package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Service class RootEntityParsers {
    private static volatile @Service RootEntityParsers instance;

    public static @Service RootEntityParsers getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new RootEntityParsers();
                }
            }
        }
        return instance;
    }

    private RootEntityParsers() {
    }

    public List<CMainEntity> parse(@Mandatory List<Token> tokens) {
        throw new UnsupportedOperationException("TODO"); // TODO
    }
}

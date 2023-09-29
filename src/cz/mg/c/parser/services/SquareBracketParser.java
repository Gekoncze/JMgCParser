package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.c.parser.components.BracketParser;
import cz.mg.c.parser.entities.groups.SquareBrackets;

public @Service class SquareBracketParser extends BracketParser {
    private static volatile @Service SquareBracketParser instance;

    public static @Service SquareBracketParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new SquareBracketParser();
                }
            }
        }
        return instance;
    }

    
    private SquareBracketParser() {
        super("square", "[", "]", SquareBrackets::new);
    }
}

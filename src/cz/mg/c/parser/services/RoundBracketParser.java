package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.c.parser.components.BracketParser;
import cz.mg.c.parser.entities.groups.RoundBrackets;

public @Service class RoundBracketParser extends BracketParser {
    private static volatile @Service RoundBracketParser instance;

    public static @Service RoundBracketParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new RoundBracketParser();
                }
            }
        }
        return instance;
    }
    
    private RoundBracketParser() {
        super("round", "(", ")", RoundBrackets::new);
    }
}

package cz.mg.c.parser.services.bracket;

import cz.mg.annotations.classes.Service;
import cz.mg.token.tokens.brackets.SquareBrackets;

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
package cz.mg.c.parser.services.bracket;

import cz.mg.annotations.classes.Service;
import cz.mg.token.tokens.brackets.RoundBrackets;

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
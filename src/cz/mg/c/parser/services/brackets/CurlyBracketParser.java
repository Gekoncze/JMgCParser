package cz.mg.c.parser.services.brackets;

import cz.mg.annotations.classes.Service;
import cz.mg.c.parser.components.BracketParser;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;

public @Service class CurlyBracketParser extends BracketParser {
    private static volatile @Service CurlyBracketParser instance;

    public static @Service CurlyBracketParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new CurlyBracketParser();
                }
            }
        }
        return instance;
    }

    private CurlyBracketParser() {
        super("curly", "{", "}", CurlyBrackets::new);
    }
}

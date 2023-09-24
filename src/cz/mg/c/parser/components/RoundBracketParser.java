package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Component;
import cz.mg.c.parser.entities.groups.RoundBrackets;

public @Component class RoundBracketParser extends BracketParser {
    public RoundBracketParser() {
        super("round", "(", ")", RoundBrackets::new);
    }
}

package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Component;
import cz.mg.c.parser.entities.groups.CurlyBrackets;

public @Component class CurlyBracketParser extends BracketParser {
    public CurlyBracketParser() {
        super("curly", "{", "}", CurlyBrackets::new);
    }
}

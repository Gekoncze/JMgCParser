package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Component;
import cz.mg.c.parser.entities.groups.SquareBrackets;

public @Component class SquareBracketParser extends BracketParser {
    public SquareBracketParser() {
        super("square", "[", "]", SquareBrackets::new);
    }
}

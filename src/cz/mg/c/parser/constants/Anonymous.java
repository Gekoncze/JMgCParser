package cz.mg.c.parser.constants;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.token.tokens.WordToken;

public @Static class Anonymous {
    public static final @Mandatory WordToken NAME = new WordToken("<anonymous>", -1);
}
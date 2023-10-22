package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Static class Anonymous {
    public static final @Mandatory NameToken NAME = new NameToken("<anonymous>", -1);
}

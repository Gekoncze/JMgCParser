package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.ReadableList;
import cz.mg.token.Token;

public @Service interface Pattern {
    boolean matches(@Mandatory ReadableList<Token> tokens);
}
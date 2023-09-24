package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Component;
import cz.mg.c.parser.entities.groups.Brackets;

public @Component interface BracketFactory {
    Brackets create();
}

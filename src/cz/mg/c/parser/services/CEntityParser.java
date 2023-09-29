package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CEntity;
import cz.mg.collections.list.ReadableList;
import cz.mg.tokenizer.entities.Token;

public @Service interface CEntityParser {
    @Mandatory CEntity parse(@Mandatory ReadableList<Token> tokens);
}

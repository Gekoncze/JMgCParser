package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.collections.list.ReadableList;
import cz.mg.tokenizer.entities.Token;

public @Service interface CMainEntityParser extends CEntityParser {
    @Override
    @Mandatory CMainEntity parse(@Mandatory ReadableList<Token> tokens);
}

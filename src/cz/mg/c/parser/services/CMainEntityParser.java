package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.tokenizer.components.TokenReader;

public @Service interface CMainEntityParser extends CEntityParser {
    @Override
    @Mandatory CMainEntity parse(@Mandatory TokenReader reader);
}

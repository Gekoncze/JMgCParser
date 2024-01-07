package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CMainEntity;

public @Service interface CMainEntityParser extends CEntityParser {
    @Override
    @Mandatory CMainEntity parse(@Mandatory TokenReader reader);
}

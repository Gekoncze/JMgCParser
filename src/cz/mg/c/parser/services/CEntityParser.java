package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.CEntity;

public @Service interface CEntityParser {
    @Mandatory CEntity parse(@Mandatory TokenReader reader);
}

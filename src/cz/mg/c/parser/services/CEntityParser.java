package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CEntity;
import cz.mg.tokenizer.components.TokenReader;

public @Service interface CEntityParser {
    @Mandatory CEntity parse(@Mandatory TokenReader reader);
}

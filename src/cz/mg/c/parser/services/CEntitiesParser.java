package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;

public @Service interface CEntitiesParser<E extends CEntity> {
    @Mandatory List<E> parse(@Mandatory TokenReader reader);
}

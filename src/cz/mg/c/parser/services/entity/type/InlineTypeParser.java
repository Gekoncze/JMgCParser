package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.types.CType;

public @Service interface InlineTypeParser {
    @Mandatory CType parse(@Mandatory TokenReader reader);
}
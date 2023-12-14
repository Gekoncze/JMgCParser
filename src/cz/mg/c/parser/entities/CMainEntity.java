package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Shared;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Entity interface CMainEntity extends CEntity {
    @Required @Shared
    WordToken getName();
}

package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Required;
import cz.mg.c.entities.CModifier;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.set.Set;
import cz.mg.token.tokens.WordToken;

public @Service class ModifiersParser {
    private static volatile @Service ModifiersParser instance;

    public static @Service ModifiersParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ModifiersParser();
                }
            }
        }
        return instance;
    }

    private ModifiersParser() {
    }

    public @Required Set<CModifier> parse(@Mandatory TokenReader reader) {
        Set<CModifier> modifiers = new Set<>();
        while (reader.has()) {
            if (reader.has("const", WordToken.class)) {
                reader.read();
                modifiers.set(CModifier.CONST);
            } else if (reader.has("static", WordToken.class)) {
                reader.read();
                modifiers.set(CModifier.STATIC);
            } else {
                break;
            }
        }
        return modifiers;
    }
}
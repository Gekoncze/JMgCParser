package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Required;
import cz.mg.c.entities.CTypeModifiers;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Service class TypeModifiersParser {
    private static volatile @Service TypeModifiersParser instance;

    public static @Service TypeModifiersParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TypeModifiersParser();
                }
            }
        }
        return instance;
    }

    private TypeModifiersParser() {
    }

    public @Required CTypeModifiers parse(@Mandatory TokenReader reader) {
        CTypeModifiers modifiers = new CTypeModifiers();
        while (reader.has()) {
            if (reader.has("const", WordToken.class)) {
                reader.read();
                modifiers.setConstant(true);
            } else if (reader.has("static", WordToken.class)) {
                reader.read();
                modifiers.setStatic(true);
            } else {
                break;
            }
        }
        return modifiers;
    }
}

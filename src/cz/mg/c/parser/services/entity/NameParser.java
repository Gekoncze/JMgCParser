package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Anonymous;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Service class NameParser {
    private static volatile @Service NameParser instance;

    public static @Service NameParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new NameParser();
                }
            }
        }
        return instance;
    }

    private NameParser() {
    }

    public @Mandatory WordToken parse(@Mandatory TokenReader reader) {
        if (reader.has(WordToken.class)) {
            return reader.read(WordToken.class);
        } else {
            return Anonymous.NAME;
        }
    }
}

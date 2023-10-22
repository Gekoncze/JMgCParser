package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Service class ConstParser {
    private static volatile @Service ConstParser instance;

    public static @Service ConstParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ConstParser();
                }
            }
        }
        return instance;
    }

    private ConstParser() {
    }

    public boolean parse(@Mandatory TokenReader reader) {
        boolean constant = false;
        while (reader.has("const", NameToken.class)) {
            reader.read();
            constant = true;
        }
        return constant;
    }
}

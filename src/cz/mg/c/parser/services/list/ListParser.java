package cz.mg.c.parser.services.list;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

public @Service class ListParser {
    private static volatile @Service ListParser instance;

    public static @Service ListParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ListParser();
                }
            }
        }
        return instance;
    }

    private ListParser() {
    }

    public @Mandatory List<List<Token>> parse(@Mandatory TokenReader reader) {
        List<List<Token>> groups = new List<>();
        groups.addLast(new List<>());
        while (reader.has()) {
            if (reader.has(",", SeparatorToken.class)) {
                reader.read();
                groups.addLast(new List<>());
            } else {
                groups.getLast().addLast(reader.read());
            }
        }
        return groups;
    }
}

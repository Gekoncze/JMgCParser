package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEnumEntry;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.services.CEntityParser;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Service class EnumEntryParser implements CEntityParser {
    private static volatile @Service EnumEntryParser instance;

    public static @Service EnumEntryParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new EnumEntryParser();
                    instance.initializerParser = InitializerParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service InitializerParser initializerParser;

    private EnumEntryParser() {
    }

    @Override
    public @Mandatory CEnumEntry parse(@Mandatory TokenReader reader) {
        CEnumEntry entry = new CEnumEntry();
        entry.setName(reader.read(WordToken.class).getText());
        entry.setExpression(initializerParser.parse(reader));
        return entry;
    }
}

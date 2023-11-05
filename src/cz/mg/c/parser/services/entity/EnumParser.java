package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Enum;
import cz.mg.c.parser.entities.EnumEntry;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.services.ListParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Service class EnumParser {
    private static volatile @Service EnumParser instance;

    public static @Service EnumParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new EnumParser();
                    instance.enumEntryParser = EnumEntryParser.getInstance();
                    instance.listParser = ListParser.getInstance();
                    instance.nameParser = NameParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service EnumEntryParser enumEntryParser;
    private @Service ListParser listParser;
    private @Service NameParser nameParser;

    private EnumParser() {
    }

    public @Mandatory Enum parse(@Mandatory TokenReader reader) {
        reader.read("enum", NameToken.class);
        Enum enom = new Enum();
        enom.setName(nameParser.parse(reader));
        if (reader.has(CurlyBrackets.class)) {
            enom.setEntries(readEntries(reader.read(CurlyBrackets.class)));
        }
        return enom;
    }

    private @Mandatory List<EnumEntry> readEntries(@Mandatory CurlyBrackets brackets) {
        List<List<Token>> values = listParser.parse(new TokenReader(brackets.getTokens()));
        List<EnumEntry> entries = new List<>();
        if (hasValues(values)) {
            for (List<Token> value : values) {
                TokenReader reader = new TokenReader(value);
                entries.addLast(enumEntryParser.parse(reader));
                reader.readEnd();
            }
        }
        return entries;
    }

    private boolean hasValues(@Mandatory List<List<Token>> values) {
        return !(values.count() == 1 && values.getFirst().count() == 0);
    }
}

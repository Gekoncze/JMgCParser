package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CEnum;
import cz.mg.c.entities.CEnumEntry;
import cz.mg.token.tokens.brackets.CurlyBrackets;
import cz.mg.c.parser.services.list.ListParser;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.WordToken;

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

    public @Mandatory CEnum parse(@Mandatory TokenReader reader) {
        reader.read("enum", WordToken.class);
        CEnum enom = new CEnum();
        enom.setName(nameParser.parse(reader));
        if (reader.has(CurlyBrackets.class)) {
            enom.setEntries(readEntries(reader.read(CurlyBrackets.class)));
        }
        return enom;
    }

    private @Mandatory List<CEnumEntry> readEntries(@Mandatory CurlyBrackets brackets) {
        List<List<Token>> values = listParser.parse(new TokenReader(brackets.getTokens()));
        List<CEnumEntry> entries = new List<>();
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
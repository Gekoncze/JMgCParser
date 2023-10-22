package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Type;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Service class InlineTypeParsers {
    private static volatile @Service InlineTypeParsers instance;

    public static @Service InlineTypeParsers getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new InlineTypeParsers();
                    instance.structTypeParser = StructTypeParser.getInstance();
                    instance.unionTypeParser = UnionTypeParser.getInstance();
                    instance.enumTypeParser = EnumTypeParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service StructTypeParser structTypeParser;
    private @Service UnionTypeParser unionTypeParser;
    private @Service EnumTypeParser enumTypeParser;

    private InlineTypeParsers() {
    }

    public @Optional Type parse(@Mandatory TokenReader reader) {
        if (reader.has("struct", NameToken.class)) {
            return structTypeParser.parse(reader);
        } else if (reader.has("union", NameToken.class)) {
            return unionTypeParser.parse(reader);
        } else if (reader.has("enum", NameToken.class)) {
            return enumTypeParser.parse(reader);
        }
        return null;
    }
}

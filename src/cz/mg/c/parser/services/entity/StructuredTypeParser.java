package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.CTypename;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.token.tokens.WordToken;

public @Service class StructuredTypeParser {
    private static volatile @Service StructuredTypeParser instance;

    public static @Service StructuredTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StructuredTypeParser();
                    instance.structParser = StructParser.getInstance();
                    instance.unionParser = UnionParser.getInstance();
                    instance.enumParser = EnumParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service StructParser structParser;
    private @Service UnionParser unionParser;
    private @Service EnumParser enumParser;

    private StructuredTypeParser() {
    }

    public @Optional CTypename parse(@Mandatory TokenReader reader) {
        if (reader.has("struct", WordToken.class)) {
            return structParser.parse(reader);
        } else if (reader.has("union", WordToken.class)) {
            return unionParser.parse(reader);
        } else if (reader.has("enum", WordToken.class)) {
            return enumParser.parse(reader);
        }
        return null;
    }
}
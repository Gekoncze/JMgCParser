package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.CTypename;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.services.entity.StructuredTypeParser;
import cz.mg.collections.set.Set;
import cz.mg.token.tokens.WordToken;

public @Service class TypeParser {
    private static volatile @Service TypeParser instance;

    public static @Service TypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new TypeParser();
                instance.modifiersParser = ModifiersParser.getInstance();
                instance.pointerTypeParser = PointerTypeParser.getInstance();
                instance.functionTypeParser = FunctionTypeParser.getInstance();
                instance.structuredTypeParser = StructuredTypeParser.getInstance();
            }
        }
        return instance;
    }

    private @Service ModifiersParser modifiersParser;
    private @Service PointerTypeParser pointerTypeParser;
    private @Service FunctionTypeParser functionTypeParser;
    private @Service StructuredTypeParser structuredTypeParser;

    public @Mandatory CTypeChain parse(@Mandatory TokenReader reader) {
        CTypeChain types = new CTypeChain(parseBaseType(reader));
        types.addFirst(pointerTypeParser.parse(reader));

        if (reader.has(functionTypeParser::matches)) {
            types = functionTypeParser.parse(reader, types);
        }

        return types;
    }

    private @Mandatory CBaseType parseBaseType(@Mandatory TokenReader reader) {
        Set<CModifier> modifiers = modifiersParser.parse(reader);
        CTypename structuredType = structuredTypeParser.parse(reader);
        CBaseType baseType = new CBaseType();
        baseType.getModifiers().setCollection(modifiers);
        baseType.setTypename(
            structuredType != null
                ? structuredType
                : new CTypename(reader.read(WordToken.class).getText())
        );
        baseType.getModifiers().setCollection(modifiersParser.parse(reader));
        return baseType;
    }
}
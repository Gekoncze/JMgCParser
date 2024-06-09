package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CTypeModifiers;
import cz.mg.c.parser.components.Modifiers;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.CTypename;
import cz.mg.c.parser.services.CEntityParser;
import cz.mg.c.parser.services.entity.type.*;
import cz.mg.token.tokens.WordToken;

public @Service class TypeParser implements CEntityParser {
    private static volatile @Service TypeParser instance;

    public static @Service TypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new TypeParser();
                instance.modifiersParser = TypeModifiersParser.getInstance();
                instance.pointerParser = PointerParser.getInstance();
                instance.inlineTypeParsers = InlineTypeParsers.getInstance();
                instance.functionTypeParser = FunctionTypeParser.getInstance();
            }
        }
        return instance;
    }

    private @Service TypeModifiersParser modifiersParser;
    private @Service PointerParser pointerParser;
    private @Service InlineTypeParsers inlineTypeParsers;
    private @Service FunctionTypeParser functionTypeParser;

    @Override
    public @Mandatory CType parse(@Mandatory TokenReader reader) {
        CTypeModifiers modifiers = modifiersParser.parse(reader);
        CType type = inlineTypeParsers.parse(reader, modifiers);
        if (type == null) {
            type = parsePlainType(reader, modifiers);
            if (reader.has(functionTypeParser::matches)) {
                type = functionTypeParser.parse(reader, type);
            }
        }
        return type;
    }

    private @Mandatory CType parsePlainType(@Mandatory TokenReader reader, @Mandatory CTypeModifiers modifiers) {
        CType type = new CType();
        type.setTypename(new CTypename(reader.read(WordToken.class).getText()));
        type.setModifiers(Modifiers.or(modifiers, modifiersParser.parse(reader)));
        type.setPointers(pointerParser.parse(reader));
        return type;
    }
}
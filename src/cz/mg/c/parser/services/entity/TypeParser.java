package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.CTypename;
import cz.mg.c.entities.types.CDataType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.services.CEntityParser;
import cz.mg.c.parser.services.entity.type.*;
import cz.mg.collections.pair.Pair;
import cz.mg.collections.set.Set;
import cz.mg.collections.set.Sets;
import cz.mg.token.tokens.WordToken;

public @Service class TypeParser implements CEntityParser {
    private static volatile @Service TypeParser instance;

    public static @Service TypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new TypeParser();
                instance.modifiersParser = ModifiersParser.getInstance();
                instance.pointerTypeParser = PointerTypeParser.getInstance();
                instance.inlineTypeParsers = InlineTypeParsers.getInstance();
                instance.functionTypeParser = FunctionTypeParser.getInstance();
                instance.typeConnector = TypeConnector.getInstance();
            }
        }
        return instance;
    }

    private @Service ModifiersParser modifiersParser;
    private @Service PointerTypeParser pointerTypeParser;
    private @Service InlineTypeParsers inlineTypeParsers;
    private @Service FunctionTypeParser functionTypeParser;
    private @Service TypeConnector typeConnector;

    @Override
    public @Mandatory CType parse(@Mandatory TokenReader reader) {
        Set<CModifier> modifiers = modifiersParser.parse(reader);
        CType type = inlineTypeParsers.parse(reader, modifiers);
        if (type == null) {
            type = parseNamedType(reader, modifiers);
            if (reader.has(functionTypeParser::matches)) {
                type = functionTypeParser.parse(reader, type);
            }
        }
        return type;
    }

    private @Mandatory CType parseNamedType(@Mandatory TokenReader reader, @Mandatory Set<CModifier> modifiers) {
        CDataType dataType = new CDataType();
        dataType.setTypename(new CTypename(reader.read(WordToken.class).getText()));
        dataType.setModifiers(Sets.union(modifiers, modifiersParser.parse(reader)));

        Pair<CPointerType, CPointerType> pointerTypes = pointerTypeParser.parse(reader);
        return typeConnector.connect(pointerTypes, dataType);
    }
}
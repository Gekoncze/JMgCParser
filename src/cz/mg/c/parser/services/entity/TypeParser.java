package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.CType;
import cz.mg.c.parser.entities.CTypename;
import cz.mg.c.parser.services.CEntityParser;
import cz.mg.c.parser.services.entity.type.ConstParser;
import cz.mg.c.parser.services.entity.type.FunctionTypeParser;
import cz.mg.c.parser.services.entity.type.InlineTypeParsers;
import cz.mg.c.parser.services.entity.type.PointerParser;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Service class TypeParser implements CEntityParser {
    private static volatile @Service TypeParser instance;

    public static @Service TypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new TypeParser();
                instance.constParser = ConstParser.getInstance();
                instance.pointerParser = PointerParser.getInstance();
                instance.inlineTypeParsers = InlineTypeParsers.getInstance();
                instance.functionTypeParser = FunctionTypeParser.getInstance();
            }
        }
        return instance;
    }

    private @Service ConstParser constParser;
    private @Service PointerParser pointerParser;
    private @Service InlineTypeParsers inlineTypeParsers;
    private @Service FunctionTypeParser functionTypeParser;

    @Override
    public @Mandatory CType parse(@Mandatory TokenReader reader) {
        boolean constant = constParser.parse(reader);
        CType type = inlineTypeParsers.parse(reader, constant);
        if (type == null) {
            type = parsePlainType(reader, constant);
            if (reader.has(functionTypeParser::matches)) {
                type = functionTypeParser.parse(reader, type);
            }
        }
        return type;
    }

    private @Mandatory CType parsePlainType(@Mandatory TokenReader reader, boolean constant) {
        CType type = new CType();
        type.setConstant(type.isConstant() | constant);
        type.setTypename(new CTypename(reader.read(WordToken.class).getText()));
        type.setConstant(type.isConstant() | constParser.parse(reader));
        type.setPointers(pointerParser.parse(reader));
        return type;
    }
}

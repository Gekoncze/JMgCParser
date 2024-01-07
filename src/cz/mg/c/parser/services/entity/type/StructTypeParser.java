package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CType;
import cz.mg.c.parser.services.entity.StructParser;

public @Service class StructTypeParser implements InlineTypeParser {
    private static volatile @Service StructTypeParser instance;

    public static @Service StructTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StructTypeParser();
                    instance.constParser = ConstParser.getInstance();
                    instance.pointerParser = PointerParser.getInstance();
                    instance.structParser = StructParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ConstParser constParser;
    private @Service PointerParser pointerParser;
    private @Service StructParser structParser;

    private StructTypeParser() {
    }

    @Override
    public @Mandatory CType parse(@Mandatory TokenReader reader) {
        CType type = new CType();
        type.setConstant(type.isConstant() | constParser.parse(reader));
        type.setTypename(structParser.parse(reader));
        type.setConstant(type.isConstant() | constParser.parse(reader));
        type.setPointers(pointerParser.parse(reader));
        return type;
    }
}

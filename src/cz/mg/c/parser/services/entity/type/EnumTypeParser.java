package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CType;
import cz.mg.c.parser.services.entity.EnumParser;

public @Service class EnumTypeParser implements InlineTypeParser {
    private static volatile @Service EnumTypeParser instance;

    public static @Service EnumTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new EnumTypeParser();
                    instance.constParser = ConstParser.getInstance();
                    instance.pointerParser = PointerParser.getInstance();
                    instance.enumParser = EnumParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ConstParser constParser;
    private @Service PointerParser pointerParser;
    private @Service EnumParser enumParser;

    private EnumTypeParser() {
    }

    @Override
    public @Mandatory CType parse(@Mandatory TokenReader reader) {
        CType type = new CType();
        type.setConstant(type.isConstant() | constParser.parse(reader));
        type.setTypename(enumParser.parse(reader));
        type.setConstant(type.isConstant() | constParser.parse(reader));
        type.setPointers(pointerParser.parse(reader));
        return type;
    }
}

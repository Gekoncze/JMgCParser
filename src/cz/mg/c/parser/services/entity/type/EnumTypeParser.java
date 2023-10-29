package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Type;
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
    public @Mandatory Type parse(@Mandatory TokenReader reader) {
        Type type = new Type();
        type.setConstant(type.isConstant() | constParser.parse(reader));
        type.setTypename(enumParser.parse(reader));
        type.setConstant(type.isConstant() | constParser.parse(reader));
        type.setPointers(pointerParser.parse(reader));
        return type;
    }
}

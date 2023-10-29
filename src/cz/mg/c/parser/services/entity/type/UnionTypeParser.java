package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.services.entity.UnionParser;

public @Service class UnionTypeParser implements InlineTypeParser {
    private static volatile @Service UnionTypeParser instance;

    public static @Service UnionTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new UnionTypeParser();
                    instance.constParser = ConstParser.getInstance();
                    instance.pointerParser = PointerParser.getInstance();
                    instance.unionParser = UnionParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ConstParser constParser;
    private @Service PointerParser pointerParser;
    private @Service UnionParser unionParser;

    private UnionTypeParser() {
    }

    @Override
    public @Mandatory Type parse(@Mandatory TokenReader reader) {
        Type type = new Type();
        type.setConstant(type.isConstant() | constParser.parse(reader));
        type.setTypename(unionParser.parse(reader));
        type.setConstant(type.isConstant() | constParser.parse(reader));
        type.setPointers(pointerParser.parse(reader));
        return type;
    }
}

package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Type;

public @Service class EnumTypeParser implements InlineTypeParser {
    private static volatile @Service EnumTypeParser instance;

    public static @Service EnumTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new EnumTypeParser();
                }
            }
        }
        return instance;
    }

    private EnumTypeParser() {
    }


    @Override
    public @Mandatory Type parse(@Mandatory TokenReader reader) {
        throw new UnsupportedOperationException(); // TODO
    }
}

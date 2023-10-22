package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Type;

public @Service class StructTypeParser implements InlineTypeParser {
    private static volatile @Service StructTypeParser instance;

    public static @Service StructTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StructTypeParser();
                }
            }
        }
        return instance;
    }

    private StructTypeParser() {
    }


    @Override
    public @Mandatory Type parse(@Mandatory TokenReader reader) {
        throw new UnsupportedOperationException(); // TODO
    }
}

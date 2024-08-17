package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CModifier;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.services.entity.StructParser;
import cz.mg.collections.set.Set;
import cz.mg.collections.set.Sets;

public @Service class StructTypeParser implements InlineTypeParser {
    private static volatile @Service StructTypeParser instance;

    public static @Service StructTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StructTypeParser();
                    instance.modifiersParser = ModifiersParser.getInstance();
                    instance.pointerTypeParser = PointerTypeParser.getInstance();
                    instance.structParser = StructParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ModifiersParser modifiersParser;
    private @Service PointerTypeParser pointerTypeParser;
    private @Service StructParser structParser;

    private StructTypeParser() {
    }

    @Override
    public @Mandatory CType parse(@Mandatory TokenReader reader) {
        CType type = new CType();
        Set<CModifier> modifiers = modifiersParser.parse(reader);
        type.setTypename(structParser.parse(reader));
        type.setModifiers(Sets.union(modifiers, modifiersParser.parse(reader)));
        type.setPointers(pointerTypeParser.parse(reader));
        return type;
    }
}
package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CModifier;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.services.entity.UnionParser;
import cz.mg.collections.set.Set;
import cz.mg.collections.set.Sets;

public @Service class UnionTypeParser implements InlineTypeParser {
    private static volatile @Service UnionTypeParser instance;

    public static @Service UnionTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new UnionTypeParser();
                    instance.modifiersParser = ModifiersParser.getInstance();
                    instance.pointerParser = PointerParser.getInstance();
                    instance.unionParser = UnionParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ModifiersParser modifiersParser;
    private @Service PointerParser pointerParser;
    private @Service UnionParser unionParser;

    private UnionTypeParser() {
    }

    @Override
    public @Mandatory CType parse(@Mandatory TokenReader reader) {
        CType type = new CType();
        Set<CModifier> modifiers = modifiersParser.parse(reader);
        type.setTypename(unionParser.parse(reader));
        type.setModifiers(Sets.union(modifiers, modifiersParser.parse(reader)));
        type.setPointers(pointerParser.parse(reader));
        return type;
    }
}
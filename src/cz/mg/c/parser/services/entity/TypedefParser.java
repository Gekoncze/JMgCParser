package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.parser.services.entity.type.TypeParser;
import cz.mg.token.tokens.brackets.SquareBrackets;
import cz.mg.c.parser.services.entity.type.ArrayTypeParser;
import cz.mg.token.tokens.WordToken;

public @Service class TypedefParser {
    private static volatile @Service TypedefParser instance;

    public static @Service TypedefParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TypedefParser();
                    instance.typeParser = TypeParser.getInstance();
                    instance.nameParser = NameParser.getInstance();
                    instance.arrayTypeParser = ArrayTypeParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;
    private @Service NameParser nameParser;
    private @Service ArrayTypeParser arrayTypeParser;

    private TypedefParser() {
    }

    public @Mandatory CTypedef parse(@Mandatory TokenReader reader) {
        CTypedef typedef = new CTypedef();
        reader.read("typedef", WordToken.class);
        CTypeChain types = typeParser.parse(reader);
        typedef.setName(nameParser.parse(reader));

        if (types.getLast() instanceof CBaseType baseType && baseType.getTypename() instanceof CFunction function) {
            typedef.setName(function.getName());
        } else if (reader.has(SquareBrackets.class)) {
            types.addFirst(arrayTypeParser.parse(reader));
        }

        typedef.setType(types.getFirst());

        return typedef;
    }
}
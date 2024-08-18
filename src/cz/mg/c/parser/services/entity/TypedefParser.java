package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.types.CWrapperType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.parser.services.entity.type.TypeConnector;
import cz.mg.c.parser.services.entity.type.TypeUnwrapper;
import cz.mg.token.tokens.brackets.SquareBrackets;
import cz.mg.c.parser.exceptions.ParseException;
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
                    instance.typeConnector = TypeConnector.getInstance();
                    instance.typeUnwrapper = TypeUnwrapper.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;
    private @Service NameParser nameParser;
    private @Service ArrayTypeParser arrayTypeParser;
    private @Service TypeConnector typeConnector;
    private @Service TypeUnwrapper typeUnwrapper;

    private TypedefParser() {
    }

    public @Mandatory CTypedef parse(@Mandatory TokenReader reader) {
        CTypedef typedef = new CTypedef();
        reader.read("typedef", WordToken.class);
        typedef.setType(typeParser.parse(reader));
        typedef.setName(nameParser.parse(reader));

        CType type = typedef.getType();

        if (typeUnwrapper.unwrap(type).getTypename() instanceof CFunction function) {
            typedef.setName(function.getName());
        } else if (reader.has(SquareBrackets.class)) {
            if (!hasArrays(type)) {
                typedef.setType(typeConnector.connect(arrayTypeParser.parse(reader), type));
            } else {
                throw new ParseException(
                    reader.read(SquareBrackets.class).getPosition(),
                    "Unexpected array combination."
                );
            }
        }

        return typedef;
    }

    private boolean hasArrays(@Mandatory CType type) {
        while (type instanceof CWrapperType wrapper) {
            if (type instanceof CArrayType) {
                return true;
            }
            type = wrapper.getType();
        }
        return false;
    }
}
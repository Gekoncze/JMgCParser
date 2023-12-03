package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Function;
import cz.mg.c.parser.entities.Typedef;
import cz.mg.c.parser.entities.brackets.SquareBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.services.entity.type.ArrayParser;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Service class TypedefParser {
    private static volatile @Service TypedefParser instance;

    public static @Service TypedefParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TypedefParser();
                    instance.typeParser = TypeParser.getInstance();
                    instance.nameParser = NameParser.getInstance();
                    instance.arrayParser = ArrayParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;
    private @Service NameParser nameParser;
    private @Service ArrayParser arrayParser;

    private TypedefParser() {
    }

    public @Mandatory Typedef parse(@Mandatory TokenReader reader) {
        Typedef typedef = new Typedef();
        reader.read("typedef", WordToken.class);
        typedef.setType(typeParser.parse(reader));
        typedef.setName(nameParser.parse(reader));

        if (typedef.getType().getTypename() instanceof Function) {
            typedef.setName(typedef.getType().getTypename().getName());
        } else if (reader.has(SquareBrackets.class)) {
            if (typedef.getType().getArrays().count() == 0) {
                typedef.getType().setArrays(arrayParser.parse(reader));
            } else {
                SquareBrackets brackets = reader.read(SquareBrackets.class);
                throw new ParseException(brackets.getPosition(), "Unexpected array combination.");
            }
        }

        return typedef;
    }
}

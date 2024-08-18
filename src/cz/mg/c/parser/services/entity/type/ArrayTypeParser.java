package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.pair.Pair;
import cz.mg.token.tokens.brackets.SquareBrackets;
import cz.mg.collections.list.List;
import cz.mg.token.Token;

public @Service class ArrayTypeParser {
    private static volatile @Service ArrayTypeParser instance;

    public static @Service ArrayTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ArrayTypeParser();
                    instance.typeConnector = TypeConnector.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TypeConnector typeConnector;

    private ArrayTypeParser() {
    }

    /**
     * Parses series of arrays. Arrays are connected into chain.
     * @return first and last array type object or null if there are no arrays to parse
     */
    public @Optional Pair<CArrayType, CArrayType> parse(@Mandatory TokenReader reader) {
        List<CArrayType> arrays = new List<>();

        while (reader.has(SquareBrackets.class)) {
            SquareBrackets brackets = reader.read(SquareBrackets.class);
            CArrayType array = new CArrayType();
            for (Token token : brackets.getTokens()) {
                array.getExpression().addLast(token);
            }
            arrays.addLast(array);
        }

        return typeConnector.connect(arrays);
    }
}
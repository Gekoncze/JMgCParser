package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.token.tokens.brackets.SquareBrackets;
import cz.mg.token.Token;

public @Service class ArrayTypeParser {
    private static volatile @Service ArrayTypeParser instance;

    public static @Service ArrayTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ArrayTypeParser();
                }
            }
        }
        return instance;
    }

    private ArrayTypeParser() {
    }

    public @Optional CTypeChain parse(@Mandatory TokenReader reader) {
        CTypeChain arrays = null;

        while (reader.has(SquareBrackets.class)) {
            SquareBrackets brackets = reader.read(SquareBrackets.class);
            CArrayType array = new CArrayType();

            for (Token token : brackets.getTokens()) {
                array.getExpression().addLast(token);
            }

            if (arrays == null) {
                arrays = new CTypeChain(array);
            } else {
                arrays.addLast(array);
            }
        }

        return arrays;
    }
}
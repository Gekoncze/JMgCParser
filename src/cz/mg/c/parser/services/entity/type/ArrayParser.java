package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CArray;
import cz.mg.c.entities.brackets.SquareBrackets;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Service class ArrayParser {
    private static volatile @Service ArrayParser instance;

    public static @Service ArrayParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new ArrayParser();
                }
            }
        }
        return instance;
    }

    private ArrayParser() {
    }

    public @Mandatory List<CArray> parse(@Mandatory TokenReader reader) {
        List<CArray> arrays = new List<>();
        while (reader.has(SquareBrackets.class)) {
            SquareBrackets brackets = reader.read(SquareBrackets.class);
            CArray array = new CArray();
            for (Token token : brackets.getTokens()) {
                array.getExpression().addLast(token);
            }
            arrays.addLast(array);
        }
        return arrays;
    }
}

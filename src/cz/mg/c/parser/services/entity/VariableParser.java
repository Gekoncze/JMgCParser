package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.Array;
import cz.mg.c.parser.entities.Variable;
import cz.mg.c.parser.entities.brackets.SquareBrackets;
import cz.mg.c.parser.services.CMainEntityParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.components.TokenReader;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Service class VariableParser implements CMainEntityParser {
    private static volatile @Service VariableParser instance;

    public static @Service VariableParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new VariableParser();
                instance.typeParser = TypeParser.getInstance();
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;

    @Override
    public @Mandatory Variable parse(@Mandatory TokenReader reader) {
        Variable variable = new Variable();
        variable.setType(typeParser.parse(reader));
        variable.setName(reader.read(NameToken.class));
        variable.setArrays(readArrays(reader));
        return variable;
    }

    private @Mandatory List<Array> readArrays(@Mandatory TokenReader reader) {
        List<Array> arrays = new List<>();
        while (reader.has(SquareBrackets.class)) {
            arrays.addLast(
                new Array(
                    new List<>(
                        reader.read(SquareBrackets.class).getTokens()
                    )
                )
            );
        }
        return arrays;
    }
}

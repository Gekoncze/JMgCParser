package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Variable;
import cz.mg.c.parser.services.CMainEntityParser;
import cz.mg.c.parser.services.entity.type.ArrayParser;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Service class VariableParser implements CMainEntityParser {
    private static volatile @Service VariableParser instance;

    public static @Service VariableParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new VariableParser();
                instance.typeParser = TypeParser.getInstance();
                instance.arrayParser = ArrayParser.getInstance();
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;
    private @Service ArrayParser arrayParser;

    @Override
    public @Mandatory Variable parse(@Mandatory TokenReader reader) {
        Variable variable = new Variable();
        variable.setType(typeParser.parse(reader));
        variable.setName(reader.read(NameToken.class));
        variable.getType().setArrays(arrayParser.parse(reader));
        return variable;
    }
}

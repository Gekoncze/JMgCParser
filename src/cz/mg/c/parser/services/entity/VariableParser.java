package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.services.CEntityParser;
import cz.mg.c.parser.services.entity.type.ArrayTypeParser;
import cz.mg.c.parser.services.entity.type.TypeConnector;
import cz.mg.token.tokens.NumberToken;
import cz.mg.token.tokens.SymbolToken;

public @Service class VariableParser implements CEntityParser {
    private static volatile @Service VariableParser instance;

    public static @Service VariableParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new VariableParser();
                instance.typeParser = TypeParser.getInstance();
                instance.arrayTypeParser = ArrayTypeParser.getInstance();
                instance.nameParser = NameParser.getInstance();
                instance.initializerParser = InitializerParser.getInstance();
                instance.typeConnector = TypeConnector.getInstance();
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;
    private @Service ArrayTypeParser arrayTypeParser;
    private @Service NameParser nameParser;
    private @Service InitializerParser initializerParser;
    private @Service TypeConnector typeConnector;

    @Override
    public @Mandatory CVariable parse(@Mandatory TokenReader reader) {
        CType type = typeParser.parse(reader);

        CVariable variable = new CVariable();
        variable.setName(nameParser.parse(reader));
        variable.setType(typeConnector.connect(arrayTypeParser.parse(reader), type));
        variable.setBit(readBitField(reader));
        variable.setExpression(initializerParser.parse(reader));
        return variable;
    }

    private @Optional Integer readBitField(@Mandatory TokenReader reader) {
        if (reader.has(":", SymbolToken.class)) {
            reader.read();
            return Integer.parseInt(reader.read(NumberToken.class).getText());
        } else {
            return null;
        }
    }
}
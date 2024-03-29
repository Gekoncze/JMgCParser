package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CType;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.services.CMainEntityParser;
import cz.mg.c.parser.services.entity.type.ArrayParser;

public @Service class VariableParser implements CMainEntityParser {
    private static volatile @Service VariableParser instance;

    public static @Service VariableParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new VariableParser();
                instance.typeParser = TypeParser.getInstance();
                instance.arrayParser = ArrayParser.getInstance();
                instance.nameParser = NameParser.getInstance();
                instance.initializerParser = InitializerParser.getInstance();
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;
    private @Service ArrayParser arrayParser;
    private @Service NameParser nameParser;
    private @Service InitializerParser initializerParser;

    @Override
    public @Mandatory CVariable parse(@Mandatory TokenReader reader) {
        CVariable variable = new CVariable();
        variable.setType(typeParser.parse(reader));
        variable.setName(nameParser.parse(reader));
        variable.getType().setArrays(arrayParser.parse(reader));
        variable.setExpression(initializerParser.parse(reader));
        return variable;
    }

    public @Mandatory CVariable parse(@Mandatory TokenReader reader, @Mandatory CType type) {
        CVariable variable = new CVariable();
        variable.setType(type);
        variable.setName(nameParser.parse(reader));
        variable.getType().setArrays(arrayParser.parse(reader));
        variable.setExpression(initializerParser.parse(reader));
        return variable;
    }
}

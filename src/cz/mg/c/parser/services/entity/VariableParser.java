package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.services.CEntitiesParser;
import cz.mg.c.parser.services.entity.type.ArrayTypeParser;
import cz.mg.c.parser.services.entity.type.TypeParser;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.NumberToken;
import cz.mg.token.tokens.SymbolToken;

public @Service class VariableParser implements CEntitiesParser<CVariable> {
    private static volatile @Service VariableParser instance;

    public static @Service VariableParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new VariableParser();
                instance.typeParser = TypeParser.getInstance();
                instance.arrayTypeParser = ArrayTypeParser.getInstance();
                instance.nameParser = NameParser.getInstance();
                instance.initializerParser = InitializerParser.getInstance();
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;
    private @Service ArrayTypeParser arrayTypeParser;
    private @Service NameParser nameParser;
    private @Service InitializerParser initializerParser;

    @Override
    public @Mandatory List<CVariable> parse(@Mandatory TokenReader reader) {
        return parse(reader, typeParser.parse(reader));
    }

    public @Mandatory List<CVariable> parse(@Mandatory TokenReader reader, @Mandatory CTypeChain typeChain) {
        if (isFunctionPointer(typeChain)) {
            return new List<>(parseFunctionPointerVariable(reader, typeChain));
        }

        List<CVariable> variables = new List<>();
        while (true) {
            CVariable variable = new CVariable();
            variable.setName(nameParser.parse(reader));
            typeChain.addFirst(arrayTypeParser.parse(reader));
            variable.setType(typeChain.getFirst());
            variable.setBit(readBitField(reader));
            variables.addLast(variable);

            if (reader.has(",", SymbolToken.class)) {
                reader.read();
            } else {
                break;
            }
        }

        List<Token> initializer = initializerParser.parse(reader);
        if (initializer != null) {
            variables.getLast().setExpression(initializer);
        }

        return variables;
    }

    public @Mandatory CVariable parseFunctionPointerVariable(@Mandatory TokenReader reader, @Mandatory CTypeChain typeChain) {
        CVariable variable = new CVariable();
        variable.setName(((CBaseType)typeChain.getLast()).getTypename().getName());
        variable.setType(typeChain.getFirst());
        variable.setExpression(initializerParser.parse(reader));
        return variable;
    }

    private boolean isFunctionPointer(@Mandatory CTypeChain typeChain) {
        return typeChain.getFirst() instanceof CPointerType pointerType
            && typeChain.getLast() instanceof CBaseType baseType
            && baseType.getTypename() instanceof CFunction;
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
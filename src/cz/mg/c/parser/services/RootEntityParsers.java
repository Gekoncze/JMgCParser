package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CModifier;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.CVariable;
import cz.mg.token.tokens.brackets.RoundBrackets;
import cz.mg.c.parser.services.entity.FunctionParser;
import cz.mg.c.parser.services.entity.TypeParser;
import cz.mg.c.parser.services.entity.TypedefParser;
import cz.mg.c.parser.services.entity.VariableParser;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.SymbolToken;
import cz.mg.token.tokens.WordToken;

public @Service class RootEntityParsers {
    private static volatile @Service RootEntityParsers instance;

    public static @Service RootEntityParsers getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new RootEntityParsers();
                    instance.typedefParser = TypedefParser.getInstance();
                    instance.typeParser = TypeParser.getInstance();
                    instance.variableParser = VariableParser.getInstance();
                    instance.functionParser = FunctionParser.getInstance();
                }
            }
        }
        return instance;
    }

    private TypedefParser typedefParser;
    private TypeParser typeParser;
    private VariableParser variableParser;
    private FunctionParser functionParser;

    private RootEntityParsers() {
    }

    public List<CEntity> parse(@Mandatory List<Token> tokens) {
        TokenReader reader = new TokenReader(tokens);
        List<CEntity> entities = new List<>();
        while (reader.has()) {
            if (isSemicolon(reader)) {
                reader.read();
            } else if (isTypedef(reader)) {
                entities.addLast(typedefParser.parse(reader));
                reader.read(";", SymbolToken.class);
            } else if (reader.has()) {
                CType type = typeParser.parse(reader);
                if (isFunction(reader)) {
                    entities.addLast(functionParser.parse(reader, type));
                } else if (isVariable(reader)) {
                    entities.addLast(variableParser.parse(reader, type));
                    reader.read(";", SymbolToken.class);
                } else if (isPlainType(type)) {
                    entities.addLast(type.getTypename());
                    reader.read(";", SymbolToken.class);
                } else if (isFunctionPointer(type)) {
                    CVariable variable = new CVariable();
                    variable.setName(type.getTypename().getName());
                    variable.setType(type);
                    entities.addLast(variable);
                    reader.read(";", SymbolToken.class);
                } else {
                    throw new UnsupportedOperationException(
                        "Unsupported type '" + type.getTypename().getName() + "'."
                    );
                }
            }
        }
        return entities;
    }

    private boolean isSemicolon(@Mandatory TokenReader reader) {
        return reader.has(";", SymbolToken.class);
    }

    private boolean isTypedef(@Mandatory TokenReader reader) {
        return reader.has("typedef", WordToken.class);
    }

    private boolean isFunction(@Mandatory TokenReader reader) {
        return reader.has(WordToken.class) && reader.hasNext(RoundBrackets.class);
    }

    private boolean isVariable(@Mandatory TokenReader reader) {
        return reader.has(WordToken.class);
    }

    private boolean isPlainType(@Mandatory CType type) {
        return type.getArrays().isEmpty()
            && type.getPointers().isEmpty()
            && !type.getModifiers().contains(CModifier.CONST);
    }

    private boolean isFunctionPointer(@Mandatory CType type) {
        return type.getTypename() instanceof CFunction
            && type.getPointers().count() > 0
            && type.getTypename().getName() != null;
    }
}
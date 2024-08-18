package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.types.CDataType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.services.entity.type.TypeUnwrapper;
import cz.mg.token.tokens.brackets.RoundBrackets;
import cz.mg.c.parser.services.entity.FunctionParser;
import cz.mg.c.parser.services.entity.TypeParser;
import cz.mg.c.parser.services.entity.TypedefParser;
import cz.mg.c.parser.services.entity.VariableParser;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.SymbolToken;
import cz.mg.token.tokens.WordToken;
import cz.mg.token.tokens.brackets.SquareBrackets;

public @Service class RootEntityParsers {
    private static volatile @Service RootEntityParsers instance;

    public static @Service RootEntityParsers getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new RootEntityParsers();
                    instance.typedefParser = TypedefParser.getInstance();
                    instance.typeParser = TypeParser.getInstance();
                    instance.typeUnwrapper = TypeUnwrapper.getInstance();
                    instance.variableParser = VariableParser.getInstance();
                    instance.functionParser = FunctionParser.getInstance();
                }
            }
        }
        return instance;
    }

    private TypedefParser typedefParser;
    private TypeParser typeParser;
    private TypeUnwrapper typeUnwrapper;
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
            } else if (isTypedefDeclaration(reader)) {
                entities.addLast(typedefParser.parse(reader));
                reader.read(";", SymbolToken.class);
            } else if (reader.has()) {
                CType type = typeParser.parse(reader);
                if (isFunctionDeclaration(reader)) {
                    entities.addLast(functionParser.parse(reader, type));
                } else if (isVariableDeclaration(reader)) {
                    entities.addLast(variableParser.parse(reader, type));
                    reader.read(";", SymbolToken.class);
                } else if (isTypenameDeclaration(type)) {
                    CDataType dataType = (CDataType) type;
                    entities.addLast(dataType.getTypename());
                    reader.read(";", SymbolToken.class);
                } else if (isFunctionVariableDeclaration(type)) {
                    CDataType dataType = (CDataType) type;
                    CVariable variable = new CVariable();
                    variable.setName(dataType.getTypename().getName());
                    variable.setType(dataType);
                    entities.addLast(variable);
                    reader.read(";", SymbolToken.class);
                } else {
                    throw new UnsupportedOperationException("Unsupported declaration.");
                }
            }
        }
        return entities;
    }

    private boolean isSemicolon(@Mandatory TokenReader reader) {
        return reader.has(";", SymbolToken.class);
    }

    private boolean isTypedefDeclaration(@Mandatory TokenReader reader) {
        return reader.has("typedef", WordToken.class);
    }

    private boolean isFunctionDeclaration(@Mandatory TokenReader reader) {
        return reader.has(WordToken.class) && reader.hasNext(RoundBrackets.class);
    }

    private boolean isVariableDeclaration(@Mandatory TokenReader reader) {
        return reader.has(WordToken.class)
            || (reader.has(RoundBrackets.class) && reader.hasNext(SquareBrackets.class));
    }

    private boolean isTypenameDeclaration(@Mandatory CType type) {
        return type instanceof CDataType dataType
            && !dataType.getModifiers().contains(CModifier.CONST);
    }

    private boolean isFunctionVariableDeclaration(@Mandatory CType type) {
        return typeUnwrapper.unwrap(type).getTypename() instanceof CFunction function
            && function.getName() != null;
    }
}
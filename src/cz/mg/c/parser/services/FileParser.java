package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.token.tokens.brackets.RoundBrackets;
import cz.mg.c.parser.services.entity.FunctionParser;
import cz.mg.c.parser.services.entity.type.TypeParser;
import cz.mg.c.parser.services.entity.TypedefParser;
import cz.mg.c.parser.services.entity.VariableParser;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.SymbolToken;
import cz.mg.token.tokens.WordToken;
import cz.mg.token.tokens.brackets.SquareBrackets;

import java.util.Objects;

public @Service class FileParser {
    private static volatile @Service FileParser instance;

    public static @Service FileParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FileParser();
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

    private FileParser() {
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
                int position = Objects.requireNonNull(reader.getItem()).get().getPosition();
                CTypeChain types = typeParser.parse(reader);
                if (isFunctionDeclaration(reader)) {
                    entities.addLast(functionParser.parse(reader, types));
                } else if (isVariableDeclaration(reader)) {
                    entities.addLast(variableParser.parse(reader, types));
                    reader.read(";", SymbolToken.class);
                } else if (isTypenameDeclaration(types)) {
                    CBaseType baseType = (CBaseType) types.getFirst();
                    entities.addLast(baseType.getTypename());
                    reader.read(";", SymbolToken.class);
                } else if (isFunctionVariableDeclaration(types)) {
                    CBaseType baseType = (CBaseType) types.getLast();
                    CVariable variable = new CVariable();
                    variable.setName(baseType.getTypename().getName());
                    variable.setType(types.getFirst());
                    entities.addLast(variable);
                    reader.read(";", SymbolToken.class);
                } else {
                    throw new ParseException(position, "Unsupported declaration.");
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

    private boolean isTypenameDeclaration(@Mandatory CTypeChain types) {
        return types.getFirst() == types.getLast()
            && types.getFirst() instanceof CBaseType baseType
            && baseType.getTypename().getName() != null
            && baseType.getModifiers().isEmpty();
    }

    private boolean isFunctionVariableDeclaration(@Mandatory CTypeChain types) {
        return types.getFirst() instanceof CPointerType
            && types.getLast() instanceof CBaseType baseType
            && baseType.getTypename() instanceof CFunction function
            && function.getName() != null;
    }
}
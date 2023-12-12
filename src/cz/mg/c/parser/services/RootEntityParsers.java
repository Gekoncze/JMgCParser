package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.services.entity.FunctionParser;
import cz.mg.c.parser.services.entity.TypeParser;
import cz.mg.c.parser.services.entity.TypedefParser;
import cz.mg.c.parser.services.entity.VariableParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;
import cz.mg.tokenizer.entities.tokens.WordToken;

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

    public List<CMainEntity> parse(@Mandatory List<Token> tokens) {
        TokenReader reader = new TokenReader(tokens);
        List<CMainEntity> entities = new List<>();
        if (isSemicolon(reader)) {
            reader.read();
        } else if (isTypedef(reader)) {
            entities.addLast(typedefParser.parse(reader));
            reader.read(";", SeparatorToken.class);
        } else if (reader.has()) {
            Type type = typeParser.parse(reader);
            if (isVariable(reader)) {
                entities.addLast(variableParser.parse(reader, type));
            } else if (isFunction(reader)) {
                entities.addLast(functionParser.parse(reader, type));
            } else if (isPlainType(reader, type)) {
                entities.addLast(type.getTypename());
                reader.read(";", SeparatorToken.class);
            } else {
                WordToken name = type.getTypename().getName();
                throw new ParseException(
                    name.getPosition(), "Unsupported type '" + name.getText() + "'."
                );
            }
        }
        return entities;
    }

    private boolean isSemicolon(@Mandatory TokenReader reader) {
        return reader.has(";", SeparatorToken.class);
    }

    private boolean isTypedef(@Mandatory TokenReader reader) {
        return reader.has("typedef", WordToken.class);
    }

    private boolean isVariable(@Mandatory TokenReader reader) {
        return reader.has(WordToken.class) && reader.hasNext(";", SeparatorToken.class);
    }

    private boolean isFunction(@Mandatory TokenReader reader) {
        return reader.has(WordToken.class) && reader.hasNext(RoundBrackets.class);
    }

    private boolean isPlainType(@Mandatory TokenReader reader, @Mandatory Type type) {
        return reader.has(";", SeparatorToken.class) && type.getArrays().isEmpty() && type.getPointers().isEmpty();
    }
}

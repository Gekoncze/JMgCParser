package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CFunction;
import cz.mg.token.tokens.brackets.CurlyBrackets;
import cz.mg.token.tokens.brackets.RoundBrackets;
import cz.mg.collections.list.List;
import cz.mg.token.Token;

public @Service class FunctionParser {
    private static volatile @Service FunctionParser instance;

    public static @Service FunctionParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FunctionParser();
                    instance.nameParser = NameParser.getInstance();
                    instance.variableListParser = VariableListParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service NameParser nameParser;
    private @Service VariableListParser variableListParser;

    private FunctionParser() {
    }

    public @Mandatory CFunction parse(@Mandatory TokenReader reader, @Mandatory CTypeChain output) {
        CFunction function = new CFunction();
        function.setOutput(output.getFirst());
        function.setName(nameParser.parse(reader));
        function.setInput(variableListParser.parse(reader.read(RoundBrackets.class)));
        if (reader.has(CurlyBrackets.class)) {
            function.setImplementation(readImplementation(reader.read(CurlyBrackets.class)));
        }
        return function;
    }

    private @Mandatory List<Token> readImplementation(@Mandatory CurlyBrackets brackets) {
        return new List<>(brackets.getTokens());
    }
}
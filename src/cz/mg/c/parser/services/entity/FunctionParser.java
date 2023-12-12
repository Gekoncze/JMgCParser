package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Function;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.c.parser.services.CMainEntityParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Service class FunctionParser implements CMainEntityParser {
    private static volatile @Service FunctionParser instance;

    public static @Service FunctionParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FunctionParser();
                    instance.typeParser = TypeParser.getInstance();
                    instance.nameParser = NameParser.getInstance();
                    instance.variableListParser = VariableListParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;
    private @Service NameParser nameParser;
    private @Service VariableListParser variableListParser;

    private FunctionParser() {
    }

    @Override
    public @Mandatory Function parse(@Mandatory TokenReader reader) {
        Function function = new Function();
        function.setOutput(typeParser.parse(reader));
        function.setName(nameParser.parse(reader));
        function.setInput(variableListParser.parse(reader.read(RoundBrackets.class)));
        if (reader.has(CurlyBrackets.class)) {
            function.setImplementation(readImplementation(reader.read(CurlyBrackets.class)));
        }
        return function;
    }

    public @Mandatory Function parse(@Mandatory TokenReader reader, Type type) {
        Function function = new Function();
        function.setOutput(type);
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

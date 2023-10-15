package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Function;
import cz.mg.c.parser.entities.Variable;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.c.parser.services.CMainEntityParser;
import cz.mg.c.parser.services.ListParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Service class FunctionParser implements CMainEntityParser {
    private static volatile @Service FunctionParser instance;

    public static @Service FunctionParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FunctionParser();
                    instance.typeParser = TypeParser.getInstance();
                    instance.variableParser = VariableParser.getInstance();
                    instance.listParser = ListParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service TypeParser typeParser;
    private @Service VariableParser variableParser;
    private @Service ListParser listParser;

    private FunctionParser() {
    }

    @Override
    public @Mandatory Function parse(@Mandatory TokenReader reader) {
        Function function = new Function();
        function.setOutput(typeParser.parse(reader));
        function.setName(reader.read(NameToken.class));
        function.setInput(readInput(reader.read(RoundBrackets.class)));
        if (reader.has(CurlyBrackets.class)) {
            function.setImplementation(readImplementation(reader.read(CurlyBrackets.class)));
        }
        return function;
    }

    private @Mandatory List<Variable> readInput(@Mandatory RoundBrackets brackets) {
        List<List<Token>> parameters = listParser.parse(new TokenReader(brackets.getTokens()));
        List<Variable> input = new List<>();
        if (hasParameters(parameters)) {
            for (List<Token> parameter : parameters) {
                TokenReader reader = new TokenReader(parameter);
                input.addLast(variableParser.parse(reader));
                reader.readEnd();
            }
        }
        return input;
    }

    private boolean hasParameters(@Mandatory List<List<Token>> parameters) {
        return !(parameters.count() == 1 && parameters.getFirst().count() == 0);
    }

    private @Mandatory List<Token> readImplementation(@Mandatory CurlyBrackets brackets) {
        return new List<>(brackets.getTokens());
    }
}

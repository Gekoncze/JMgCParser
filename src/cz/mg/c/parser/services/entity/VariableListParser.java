package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Variable;
import cz.mg.c.parser.entities.brackets.Brackets;
import cz.mg.c.parser.services.list.ListParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Service class VariableListParser {
    private static volatile @Service VariableListParser instance;

    public static @Service VariableListParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new VariableListParser();
                instance.listParser = ListParser.getInstance();
                instance.variableParser = VariableParser.getInstance();
            }
        }
        return instance;
    }

    private @Service ListParser listParser;
    private @Service VariableParser variableParser;

    public @Mandatory List<Variable> parse(@Mandatory Brackets brackets) {
        List<List<Token>> entries = listParser.parse(new TokenReader(brackets.getTokens()));
        List<Variable> variables = new List<>();
        if (!empty(entries)) {
            for (List<Token> entry : entries) {
                TokenReader reader = new TokenReader(entry);
                variables.addLast(variableParser.parse(reader));
                reader.readEnd();
            }
        }
        return variables;
    }

    private boolean empty(@Mandatory List<List<Token>> entries) {
        return entries.count() == 1 && entries.getFirst().count() == 0;
    }
}

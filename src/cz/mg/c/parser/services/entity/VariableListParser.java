package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CVariable;
import cz.mg.token.tokens.brackets.Brackets;
import cz.mg.c.parser.services.list.ListParser;
import cz.mg.collections.list.List;
import cz.mg.token.Token;

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

    public @Mandatory List<CVariable> parse(@Mandatory Brackets brackets) {
        List<List<Token>> entries = listParser.parse(new TokenReader(brackets.getTokens()));
        List<CVariable> variables = new List<>();
        if (!empty(entries)) {
            for (List<Token> entry : entries) {
                TokenReader reader = new TokenReader(entry);
                variables.addCollectionLast(variableParser.parse(reader));
                reader.readEnd();
            }
        }
        return variables;
    }

    private boolean empty(@Mandatory List<List<Token>> entries) {
        return entries.count() == 1 && entries.getFirst().count() == 0;
    }
}
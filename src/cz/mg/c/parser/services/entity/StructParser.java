package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Struct;
import cz.mg.c.parser.entities.Variable;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.services.list.SemicolonParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Service class StructParser {
    private static volatile @Service StructParser instance;

    public static @Service StructParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StructParser();
                    instance.variableParser = VariableParser.getInstance();
                    instance.semicolonParser = SemicolonParser.getInstance();
                    instance.nameParser = NameParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service VariableParser variableParser;
    private @Service SemicolonParser semicolonParser;
    private @Service NameParser nameParser;

    private StructParser() {
    }

    public @Mandatory Struct parse(@Mandatory TokenReader reader) {
        reader.read("struct", WordToken.class);
        Struct struct = new Struct();
        struct.setName(nameParser.parse(reader));
        if (reader.has(CurlyBrackets.class)) {
            struct.setVariables(readVariables(reader.read(CurlyBrackets.class)));
        }
        return struct;
    }

    private @Mandatory List<Variable> readVariables(CurlyBrackets brackets) {
        List<List<Token>> groups = semicolonParser.parse(brackets.getTokens());
        List<Variable> variables = new List<>();
        for (List<Token> group : groups) {
            TokenReader reader = new TokenReader(group);
            variables.addLast(variableParser.parse(reader));
            reader.readEnd();
        }
        return variables;
    }
}

package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Statement;
import cz.mg.c.parser.entities.Struct;
import cz.mg.c.parser.entities.Variable;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.services.statement.StatementParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Service class StructParser {
    private static volatile @Service StructParser instance;

    public static @Service StructParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StructParser();
                    instance.variableParser = VariableParser.getInstance();
                    instance.statementParser = StatementParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service VariableParser variableParser;
    private @Service StatementParser statementParser;

    private StructParser() {
    }

    public @Mandatory Struct parse(@Mandatory TokenReader reader) {
        reader.read("struct", NameToken.class);
        Struct struct = new Struct();
        struct.setName(reader.read(NameToken.class));
        if (reader.has(CurlyBrackets.class)) {
            struct.setVariables(readVariables(reader.read(CurlyBrackets.class)));
        }
        return struct;
    }

    private @Mandatory List<Variable> readVariables(CurlyBrackets brackets) {
        List<Statement> statements = statementParser.parse(brackets.getTokens());
        List<Variable> variables = new List<>();
        for (Statement statement : statements) {
            variables.addLast(variableParser.parse(new TokenReader(statement.getTokens())));
        }
        return variables;
    }
}

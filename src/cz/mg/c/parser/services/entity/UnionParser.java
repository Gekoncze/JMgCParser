package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Statement;
import cz.mg.c.parser.entities.Union;
import cz.mg.c.parser.entities.Variable;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.services.statement.StatementParser;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Service class UnionParser {
    private static volatile @Service UnionParser instance;

    public static @Service UnionParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new UnionParser();
                    instance.variableParser = VariableParser.getInstance();
                    instance.statementParser = StatementParser.getInstance();
                    instance.nameParser = NameParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service VariableParser variableParser;
    private @Service StatementParser statementParser;
    private @Service NameParser nameParser;

    private UnionParser() {
    }

    public @Mandatory Union parse(@Mandatory TokenReader reader) {
        reader.read("union", WordToken.class);
        Union union = new Union();
        union.setName(nameParser.parse(reader));
        if (reader.has(CurlyBrackets.class)) {
            union.setVariables(readVariables(reader.read(CurlyBrackets.class)));
        }
        return union;
    }

    private @Mandatory List<Variable> readVariables(CurlyBrackets brackets) {
        List<Statement> statements = statementParser.parse(brackets.getTokens());
        List<Variable> variables = new List<>();
        for (Statement statement : statements) {
            TokenReader reader = new TokenReader(statement.getTokens());
            variables.addLast(variableParser.parse(reader));
            reader.readEnd();
        }
        return variables;
    }
}

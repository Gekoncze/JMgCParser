package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CUnion;
import cz.mg.c.entities.CVariable;
import cz.mg.c.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.services.list.SemicolonParser;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.WordToken;

public @Service class UnionParser {
    private static volatile @Service UnionParser instance;

    public static @Service UnionParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new UnionParser();
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

    private UnionParser() {
    }

    public @Mandatory CUnion parse(@Mandatory TokenReader reader) {
        reader.read("union", WordToken.class);
        CUnion union = new CUnion();
        union.setName(nameParser.parse(reader));
        if (reader.has(CurlyBrackets.class)) {
            union.setVariables(readVariables(reader.read(CurlyBrackets.class)));
        }
        return union;
    }

    private @Mandatory List<CVariable> readVariables(CurlyBrackets brackets) {
        List<List<Token>> groups = semicolonParser.parse(brackets.getTokens());
        List<CVariable> variables = new List<>();
        for (List<Token> group : groups) {
            TokenReader reader = new TokenReader(group);
            variables.addLast(variableParser.parse(reader));
            reader.readEnd();
        }
        return variables;
    }
}
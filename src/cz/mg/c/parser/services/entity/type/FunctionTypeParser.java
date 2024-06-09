package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.types.CType;
import cz.mg.token.tokens.brackets.RoundBrackets;
import cz.mg.c.parser.services.entity.VariableListParser;
import cz.mg.token.Token;
import cz.mg.token.tokens.SymbolToken;
import cz.mg.token.tokens.WordToken;

public @Service class FunctionTypeParser {
    private static volatile @Service FunctionTypeParser instance;

    public static @Service FunctionTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FunctionTypeParser();
                    instance.pointerParser = PointerParser.getInstance();
                    instance.arrayParser = ArrayParser.getInstance();
                    instance.variableListParser = VariableListParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service PointerParser pointerParser;
    private @Service ArrayParser arrayParser;
    private @Service VariableListParser variableListParser;

    private FunctionTypeParser() {
    }

    public boolean matches(@Mandatory Token token) {
        if (token instanceof RoundBrackets) {
            RoundBrackets brackets = (RoundBrackets) token;
            if (!brackets.getTokens().isEmpty()) {
                token = brackets.getTokens().getFirst();
                return token instanceof SymbolToken && token.getText().startsWith("*");
            }
        }

        return false;
    }

    public @Mandatory CType parse(@Mandatory TokenReader reader, @Mandatory CType output) {
        TokenReader bracketReader = new TokenReader(reader.read(RoundBrackets.class).getTokens());
        CFunction function = new CFunction();
        function.setOutput(output);
        CType type = new CType();
        type.setTypename(function);
        type.setPointers(pointerParser.parse(bracketReader));
        function.setName(bracketReader.read(WordToken.class).getText());
        type.setArrays(arrayParser.parse(bracketReader));
        function.setInput(variableListParser.parse(reader.read(RoundBrackets.class)));
        bracketReader.readEnd();
        return type;
    }
}
package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.types.*;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CFunction;
import cz.mg.c.parser.services.entity.NameParser;
import cz.mg.token.tokens.brackets.RoundBrackets;
import cz.mg.c.parser.services.entity.VariableListParser;
import cz.mg.token.Token;
import cz.mg.token.tokens.SymbolToken;

public @Service class FunctionTypeParser {
    private static volatile @Service FunctionTypeParser instance;

    public static @Service FunctionTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FunctionTypeParser();
                    instance.pointerTypeParser = PointerTypeParser.getInstance();
                    instance.arrayTypeParser = ArrayTypeParser.getInstance();
                    instance.variableListParser = VariableListParser.getInstance();
                    instance.nameParser = NameParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service PointerTypeParser pointerTypeParser;
    private @Service ArrayTypeParser arrayTypeParser;
    private @Service VariableListParser variableListParser;
    private @Service NameParser nameParser;

    private FunctionTypeParser() {
    }

    public boolean matches(@Mandatory Token token) {
        if (token instanceof RoundBrackets brackets) {
            if (!brackets.getTokens().isEmpty()) {
                token = brackets.getTokens().getFirst();
                return token instanceof SymbolToken
                    && token.getText().startsWith("*");
            }
        }
        return false;
    }

    public @Mandatory CTypeChain parse(@Mandatory TokenReader reader, @Mandatory CTypeChain output) {
        CFunction function = new CFunction();
        function.setOutput(output.getFirst());

        TokenReader bracketReader = new TokenReader(reader.read(RoundBrackets.class).getTokens());

        CTypeChain pointerTypes = pointerTypeParser.parse(bracketReader);

        function.setName(nameParser.parse(bracketReader));

        CTypeChain arrayTypes = arrayTypeParser.parse(bracketReader);
        function.setInput(variableListParser.parse(reader.read(RoundBrackets.class)));

        bracketReader.readEnd();

        CBaseType baseType = new CBaseType();
        baseType.setTypename(function);

        CTypeChain types = new CTypeChain(baseType);
        types.addFirst(pointerTypes);
        types.addFirst(arrayTypes);
        return types;
    }
}
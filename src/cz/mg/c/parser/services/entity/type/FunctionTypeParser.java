package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.types.*;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CFunction;
import cz.mg.collections.pair.Pair;
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
                    instance.pointerTypeParser = PointerTypeParser.getInstance();
                    instance.arrayTypeParser = ArrayTypeParser.getInstance();
                    instance.variableListParser = VariableListParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service PointerTypeParser pointerTypeParser;
    private @Service ArrayTypeParser arrayTypeParser;
    private @Service VariableListParser variableListParser;

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

    public @Mandatory CType parse(@Mandatory TokenReader reader, @Mandatory CType output) {
        CFunction function = new CFunction();
        function.setOutput(output);

        TokenReader bracketReader = new TokenReader(reader.read(RoundBrackets.class).getTokens());

        Pair<CPointerType, CPointerType> pointerPair = pointerTypeParser.parse(bracketReader);
        function.setName(bracketReader.read(WordToken.class).getText());

        Pair<CArrayType, CArrayType> arrayPair = arrayTypeParser.parse(bracketReader);
        function.setInput(variableListParser.parse(reader.read(RoundBrackets.class)));

        CDataType dataType = new CDataType();
        dataType.setTypename(function);

        bracketReader.readEnd();

        return connect(arrayPair, pointerPair, dataType);
    }

    private CType connect(
        @Optional Pair<CArrayType, CArrayType> arrayPair,
        @Optional Pair<CPointerType, CPointerType> pointerPair,
        @Mandatory CDataType type
    ) {
        if (arrayPair != null && pointerPair != null) {
            arrayPair.getValue().setType(pointerPair.getKey());
            pointerPair.getValue().setType(type);
            return arrayPair.getKey();
        } else if (arrayPair != null) {
            arrayPair.getValue().setType(type);
            return arrayPair.getKey();
        } else if (pointerPair != null) {
            pointerPair.getValue().setType(type);
            return pointerPair.getKey();
        } else {
            return type;
        }
    }
}
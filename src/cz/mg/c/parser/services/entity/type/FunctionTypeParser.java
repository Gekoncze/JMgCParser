package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.OperatorToken;

public @Service class FunctionTypeParser {
    private static volatile @Service FunctionTypeParser instance;

    public static @Service FunctionTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new FunctionTypeParser();
                }
            }
        }
        return instance;
    }

    private FunctionTypeParser() {
    }

    public boolean matches(@Mandatory Token token) {
        if (token instanceof RoundBrackets) {
            RoundBrackets brackets = (RoundBrackets) token;
            if (!brackets.getTokens().isEmpty()) {
                token = brackets.getTokens().getFirst();
                return token instanceof OperatorToken && token.getText().startsWith("*");
            }
        }

        return false;
    }

    public @Mandatory Type parse(@Mandatory TokenReader reader, @Mandatory Type output) {
        RoundBrackets brackets = reader.read(RoundBrackets.class);
        throw new UnsupportedOperationException(); // TODO
    }
}

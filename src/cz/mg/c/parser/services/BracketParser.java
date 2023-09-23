package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.groups.CurlyBrackets;
import cz.mg.c.parser.entities.groups.RoundBrackets;
import cz.mg.c.parser.entities.groups.SquareBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.components.TokenReader;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.BracketToken;

public @Service class BracketParser {
    private static volatile @Service BracketParser instance;

    public static @Service BracketParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new BracketParser();
                }
            }
        }
        return instance;
    }

    private BracketParser() {
    }

    public @Mandatory List<Token> parse(@Mandatory List<Token> input) {
        TokenReader reader = new TokenReader(input, ParseException::new);
        List<Token> result = new List<>();
        parse(reader, result, '\0');
        return result;
    }

    private void parse(@Mandatory TokenReader reader, @Mandatory List<Token> output, char closing) {
        while (reader.has()) {
            if (reader.has(BracketToken.class)) {
                char ch = reader.read().getText().charAt(0);
                if (ch == closing) {
                    return;
                } else if (ch == '(') {
                    output.addLast(parseRoundBrackets(reader));
                } else if (ch == '[') {
                    output.addLast(parseSquareBrackets(reader));
                } else if (ch == '{') {
                    output.addLast(parseCurlyBrackets(reader));
                } else {
                    Token token = reader.read();
                    throw new ParseException(
                        token.getPosition(),
                        "Unexpected bracket type " + token.getClass().getSimpleName() + "."
                    );
                }
            } else {
                output.addLast(reader.read());
            }
        }
    }

    private @Mandatory RoundBrackets parseRoundBrackets(@Mandatory TokenReader reader) {
        RoundBrackets brackets = new RoundBrackets();
        parse(reader, brackets.getTokens(), ')');
        return brackets;
    }

    private @Mandatory SquareBrackets parseSquareBrackets(@Mandatory TokenReader reader) {
        SquareBrackets brackets = new SquareBrackets();
        parse(reader, brackets.getTokens(), ']');
        return brackets;
    }

    private @Mandatory CurlyBrackets parseCurlyBrackets(@Mandatory TokenReader reader) {
        CurlyBrackets brackets = new CurlyBrackets();
        parse(reader, brackets.getTokens(), '}');
        return brackets;
    }
}

package cz.mg.c.parser.services.bracket;

import cz.mg.annotations.classes.Base;
import cz.mg.annotations.classes.Component;
import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.brackets.Brackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.collections.list.ReadableList;
import cz.mg.collections.list.WriteableList;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.BracketToken;

public @Base @Service class BracketParser {
    private final @Mandatory String name;
    private final @Mandatory String openingBracket;
    private final @Mandatory String closingBracket;
    private final @Mandatory BracketFactory bracketFactory;

    public BracketParser(
        @Mandatory String name,
        @Mandatory String openingBracket,
        @Mandatory String closingBracket,
        @Mandatory BracketFactory bracketFactory
    ) {
        this.name = name;
        this.openingBracket = openingBracket;
        this.closingBracket = closingBracket;
        this.bracketFactory = bracketFactory;
    }

    public @Mandatory List<Token> parse(@Mandatory ReadableList<Token> input) {
        TokenReader reader = new TokenReader(input);
        List<Token> output = new List<>();
        parse(reader, output, null);
        return output;
    }

    private void parse(
        @Mandatory TokenReader reader,
        @Mandatory WriteableList<Token> output,
        @Optional Integer openingPosition
    ) {
        while (reader.has()) {
            if (reader.has(openingBracket, BracketToken.class)) {
                output.addLast(parse(reader));
            } else if (reader.has(closingBracket, BracketToken.class)) {
                int position = reader.read().getPosition();
                if (openingPosition != null) {
                    return;
                } else {
                    throw new ParseException(position, "Missing left " + name + " parenthesis.");
                }
            } else {
                output.addLast(parse(reader.read()));
            }
        }

        if (openingPosition != null) {
            throw new ParseException(openingPosition, "Missing right " + name + " parenthesis.");
        }
    }

    private @Mandatory Brackets parse(@Mandatory TokenReader reader) {
        Brackets brackets = bracketFactory.create();
        brackets.setPosition(reader.read().getPosition());
        brackets.setText("");
        parse(reader, brackets.getTokens(), brackets.getPosition());
        return brackets;
    }

    private @Mandatory Token parse(@Mandatory Token token) {
        if (token instanceof Brackets) {
            Brackets brackets = (Brackets) token;
            brackets.setTokens(parse(brackets.getTokens()));
        }
        return token;
    }

    protected @Component interface BracketFactory {
        Brackets create();
    }
}

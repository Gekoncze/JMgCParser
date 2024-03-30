package cz.mg.c.parser.services.bracket;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.collections.list.ReadableList;
import cz.mg.token.Token;

public @Service class BracketParsers {
    private static volatile @Service BracketParsers instance;

    public static @Service BracketParsers getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new BracketParsers();
                    instance.roundBracketParser = RoundBracketParser.getInstance();
                    instance.squareBracketParser = SquareBracketParser.getInstance();
                    instance.curlyBracketParser = CurlyBracketParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service RoundBracketParser roundBracketParser;
    private @Service SquareBracketParser squareBracketParser;
    private @Service CurlyBracketParser curlyBracketParser;

    private BracketParsers() {
    }

    public @Mandatory List<Token> parse(@Mandatory ReadableList<Token> tokens) {
        return roundBracketParser.parse(
            squareBracketParser.parse(
                curlyBracketParser.parse(tokens)
            )
        );
    }
}
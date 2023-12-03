package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Array;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Test class ArrayParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + ArrayParserTest.class.getSimpleName() + " ... ");

        ArrayParserTest test = new ArrayParserTest();
        test.testParseEmpty();
        test.testParseSingle();
        test.testParseMultiple();
        test.testParseRemainingTokens();

        System.out.println("OK");
    }

    private final @Service ArrayParser parser = ArrayParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertEquals(true, parser.parse(new TokenReader(new List<>())).isEmpty());
        Assert.assertEquals(true, parser.parse(new TokenReader(new List<>(new WordToken("foo", 0)))).isEmpty());
    }

    private void testParseSingle() {
        List<Token> tokens = new List<>(b.squareBrackets(new NumberToken("5", 2)));
        List<Array> arrays = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(1, arrays.count());
        Assert.assertEquals(1, arrays.getFirst().getExpression().count());
        Assert.assertEquals("5", arrays.getFirst().getExpression().getFirst().getText());
    }

    private void testParseMultiple() {
        List<Token> tokens = new List<>(
            b.squareBrackets(),
            b.squareBrackets(new NumberToken("7", 12)),
            b.squareBrackets(
                new NumberToken("1", 22),
                new OperatorToken("+", 24),
                new NumberToken("3", 26)
            )
        );
        List<Array> arrays = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(3, arrays.count());
        Assert.assertEquals(0, arrays.get(0).getExpression().count());
        Assert.assertEquals(1, arrays.get(1).getExpression().count());
        Assert.assertEquals(3, arrays.get(2).getExpression().count());
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(b.squareBrackets(), new WordToken("foo", 2));
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
        Assert.assertEquals("foo", reader.read().getText());
    }
}

package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CArray;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

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

    private final @Service ArrayTypeParser parser = ArrayTypeParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertEquals(true, parser.parse(new TokenReader(new List<>())).isEmpty());
        Assert.assertEquals(true, parser.parse(new TokenReader(new List<>(f.word("foo")))).isEmpty());
    }

    private void testParseSingle() {
        List<Token> tokens = new List<>(b.squareBrackets(f.number("5")));
        List<CArray> arrays = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(1, arrays.count());
        Assert.assertEquals(1, arrays.getFirst().getExpression().count());
        Assert.assertEquals("5", arrays.getFirst().getExpression().getFirst().getText());
    }

    private void testParseMultiple() {
        List<Token> tokens = new List<>(
            b.squareBrackets(),
            b.squareBrackets(f.number("7")),
            b.squareBrackets(
                f.number("1"),
                f.symbol("+"),
                f.number("3")
            )
        );
        List<CArray> arrays = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(3, arrays.count());
        Assert.assertEquals(0, arrays.get(0).getExpression().count());
        Assert.assertEquals(1, arrays.get(1).getExpression().count());
        Assert.assertEquals(3, arrays.get(2).getExpression().count());
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(b.squareBrackets(), f.word("foo"));
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
        Assert.assertEquals("foo", reader.read().getText());
    }
}
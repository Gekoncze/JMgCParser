package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.DoubleQuoteToken;
import cz.mg.tokenizer.entities.tokens.NameToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;

public @Test class ConstParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + ConstParserTest.class.getSimpleName() + " ... ");

        ConstParserTest test = new ConstParserTest();
        test.testParseEmpty();
        test.testParseSingle();
        test.testParseMultiple();
        test.testParseRemainingTokens();
        test.testParseNoRemainingTokens();

        System.out.println("OK");
    }

    private final @Service ConstParser parser = ConstParser.getInstance();

    private void testParseEmpty() {
        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>()))
        );
    }

    private void testParseSingle() {
        Assert.assertEquals(
            true,
            parser.parse(new TokenReader(new List<>(new NameToken("const", 0))))
        );

        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>(new DoubleQuoteToken("const", 0))))
        );

        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>(new NumberToken("0", 0))))
        );
    }

    private void testParseMultiple() {
        Assert.assertEquals(
            true,
            parser.parse(new TokenReader(new List<>(
                new NameToken("const", 0),
                new NameToken("const", 0),
                new NameToken("const", 0)
            )))
        );

        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>(
                new NameToken("foo", 0),
                new NameToken("bar", 0),
                new NameToken("const", 0)
            )))
        );

        Assert.assertEquals(
            true,
            parser.parse(new TokenReader(new List<>(
                new NameToken("const", 0),
                new NameToken("foo", 0),
                new NameToken("bar", 0)
            )))
        );

        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>(
                new NameToken("foo", 0),
                new NameToken("bar", 0),
                new NameToken("foobar", 0)
            )))
        );
    }

    private void testParseRemainingTokens() {
        List<Token> input = new List<>(
            new NameToken("const", 0),
            new NameToken("foo", 0)
        );
        TokenReader reader = new TokenReader(input);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has("foo"));
    }

    private void testParseNoRemainingTokens() {
        List<Token> input = new List<>(
            new NameToken("const", 0),
            new NameToken("const", 0)
        );
        TokenReader reader = new TokenReader(input);
        parser.parse(reader);
        Assert.assertEquals(false, reader.has());
    }
}

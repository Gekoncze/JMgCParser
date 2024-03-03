package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.test.TokenFactory;

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
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>()))
        );
    }

    private void testParseSingle() {
        Assert.assertEquals(
            true,
            parser.parse(new TokenReader(new List<>(f.word("const"))))
        );

        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>(f.doubleQuote("const"))))
        );

        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>(f.number("0"))))
        );
    }

    private void testParseMultiple() {
        Assert.assertEquals(
            true,
            parser.parse(new TokenReader(new List<>(
                f.word("const"),
                f.word("const"),
                f.word("const")
            )))
        );

        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>(
                f.word("foo"),
                f.word("bar"),
                f.word("const")
            )))
        );

        Assert.assertEquals(
            true,
            parser.parse(new TokenReader(new List<>(
                f.word("const"),
                f.word("foo"),
                f.word("bar")
            )))
        );

        Assert.assertEquals(
            false,
            parser.parse(new TokenReader(new List<>(
                f.word("foo"),
                f.word("bar"),
                f.word("foobar")
            )))
        );
    }

    private void testParseRemainingTokens() {
        List<Token> input = new List<>(
            f.word("const"),
            f.word("foo")
        );
        TokenReader reader = new TokenReader(input);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has("foo"));
    }

    private void testParseNoRemainingTokens() {
        List<Token> input = new List<>(
            f.word("const"),
            f.word("const")
        );
        TokenReader reader = new TokenReader(input);
        parser.parse(reader);
        Assert.assertEquals(false, reader.has());
    }
}

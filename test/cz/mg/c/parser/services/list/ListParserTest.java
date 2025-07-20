package cz.mg.c.parser.services.list;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.token.test.TokenFactory;
import cz.mg.token.test.TokenAssertions;

public @Test class ListParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + ListParserTest.class.getSimpleName() + " ... ");

        ListParserTest test = new ListParserTest();
        test.testParseEmpty();
        test.testParseSingle();
        test.testParseMultiple();
        test.testParseMultipleEmpty();

        System.out.println("OK");
    }

    private final @Service ListParser parser = ListParser.getInstance();
    private final @Service TokenAssertions assertions = TokenAssertions.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        List<Token> input = new List<>();
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(true, output.getFirst().isEmpty());
    }

    private void testParseSingle() {
        List<Token> input = new List<>(f.word("foo"));
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(1, output.getFirst().count());
        Assert.assertEquals("foo", output.getFirst().getFirst().getText());
    }

    private void testParseMultiple() {
        List<Token> input = new List<>(
            f.word("foo"),
            f.word("bar"),
            f.symbol(","),
            f.symbol("."),
            f.symbol(","),
            f.number("11"),
            f.symbol("+"),
            f.number("0")
        );
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(3, output.count());
        assertions.assertEquals(new List<>(
            f.word("foo"),
            f.word("bar")
        ), output.get(0));
        assertions.assertEquals(new List<>(
            f.symbol(".")
        ), output.get(1));
        assertions.assertEquals(new List<>(
            f.number("11"),
            f.symbol("+"),
            f.number("0")
        ), output.get(2));
    }

    private void testParseMultipleEmpty() {
        List<Token> input = new List<>(f.symbol(","), f.symbol(","));
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(3, output.count());
        Assert.assertEquals(0, output.get(0).count());
        Assert.assertEquals(0, output.get(1).count());
        Assert.assertEquals(0, output.get(2).count());
    }
}
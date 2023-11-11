package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.WordToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;
import cz.mg.tokenizer.test.TokenValidator;

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
    private final @Service TokenValidator validator = TokenValidator.getInstance();

    private void testParseEmpty() {
        List<Token> input = new List<>();
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(true, output.getFirst().isEmpty());
    }

    private void testParseSingle() {
        List<Token> input = new List<>(new WordToken("foo", 2));
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(1, output.getFirst().count());
        Assert.assertEquals("foo", output.getFirst().getFirst().getText());
    }

    private void testParseMultiple() {
        List<Token> input = new List<>(
            new WordToken("foo", 0),
            new WordToken("bar", 4),
            new SeparatorToken(",", 8),
            new SeparatorToken(".", 9),
            new SeparatorToken(",", 10),
            new NumberToken("11", 12),
            new OperatorToken("+", 14),
            new NumberToken("0", 16)
        );
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(3, output.count());
        validator.assertEquals(new List<>(
            new WordToken("foo", 0),
            new WordToken("bar", 4)
        ), output.get(0));
        validator.assertEquals(new List<>(
            new SeparatorToken(".", 9)
        ), output.get(1));
        validator.assertEquals(new List<>(
            new NumberToken("11", 12),
            new OperatorToken("+", 14),
            new NumberToken("0", 16)
        ), output.get(2));
    }

    private void testParseMultipleEmpty() {
        List<Token> input = new List<>(new SeparatorToken(",", 2), new SeparatorToken(",", 2));
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(3, output.count());
        Assert.assertEquals(0, output.get(0).count());
        Assert.assertEquals(0, output.get(1).count());
        Assert.assertEquals(0, output.get(2).count());
    }
}

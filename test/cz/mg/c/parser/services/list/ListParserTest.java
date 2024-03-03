package cz.mg.c.parser.services.list;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.test.TokenFactory;
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
            f.separator(","),
            f.separator("."),
            f.separator(","),
            f.number("11"),
            f.operator("+"),
            f.number("0")
        );
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(3, output.count());
        validator.assertEquals(new List<>(
            f.word("foo"),
            f.word("bar")
        ), output.get(0));
        validator.assertEquals(new List<>(
            f.separator(".")
        ), output.get(1));
        validator.assertEquals(new List<>(
            f.number("11"),
            f.operator("+"),
            f.number("0")
        ), output.get(2));
    }

    private void testParseMultipleEmpty() {
        List<Token> input = new List<>(f.separator(","), f.separator(","));
        List<List<Token>> output = parser.parse(new TokenReader(input));
        Assert.assertEquals(3, output.count());
        Assert.assertEquals(0, output.get(0).count());
        Assert.assertEquals(0, output.get(1).count());
        Assert.assertEquals(0, output.get(2).count());
    }
}

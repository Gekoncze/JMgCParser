package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.EnumEntry;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.WordToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;

public @Test class EnumEntryParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + EnumEntryParserTest.class.getSimpleName() + " ... ");

        EnumEntryParserTest test = new EnumEntryParserTest();
        test.testEmpty();
        test.testSimple();
        test.testExpression();

        System.out.println("OK");
    }

    private final @Service EnumEntryParser parser = EnumEntryParser.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testSimple() {
        List<Token> input = new List<>(
            new WordToken("VALUE", 0)
        );
        EnumEntry entry = parser.parse(new TokenReader(input));
        Assert.assertEquals("VALUE", entry.getName().getText());
        Assert.assertNull(entry.getExpression());
    }

    private void testExpression() {
        List<Token> input = new List<>(
            new WordToken("COMPLEX_VALUE", 0),
            new OperatorToken("=", 20),
            new NumberToken("11", 22),
            new OperatorToken("+", 24),
            new NumberToken("2", 25)
        );
        EnumEntry entry = parser.parse(new TokenReader(input));
        Assert.assertEquals("COMPLEX_VALUE", entry.getName().getText());
        Assert.assertNotNull(entry.getExpression());
        Assert.assertEquals(3, entry.getExpression().count());
        Assert.assertEquals("11", entry.getExpression().get(0).getText());
        Assert.assertEquals("+", entry.getExpression().get(1).getText());
        Assert.assertEquals("2", entry.getExpression().get(2).getText());
    }
}

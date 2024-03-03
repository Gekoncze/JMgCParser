package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CEnumEntry;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.test.TokenFactory;

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
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testSimple() {
        List<Token> input = new List<>(
            f.word("VALUE")
        );
        CEnumEntry entry = parser.parse(new TokenReader(input));
        Assert.assertEquals("VALUE", entry.getName());
        Assert.assertNull(entry.getExpression());
    }

    private void testExpression() {
        List<Token> input = new List<>(
            f.word("COMPLEX_VALUE"),
            f.operator("="),
            f.number("11"),
            f.operator("+"),
            f.number("2")
        );
        CEnumEntry entry = parser.parse(new TokenReader(input));
        Assert.assertEquals("COMPLEX_VALUE", entry.getName());
        Assert.assertNotNull(entry.getExpression());
        Assert.assertEquals(3, entry.getExpression().count());
        Assert.assertEquals("11", entry.getExpression().get(0).getText());
        Assert.assertEquals("+", entry.getExpression().get(1).getText());
        Assert.assertEquals("2", entry.getExpression().get(2).getText());
    }
}

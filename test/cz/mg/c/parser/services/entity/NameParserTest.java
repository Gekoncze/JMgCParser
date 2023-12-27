package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Test class NameParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + NameParserTest.class.getSimpleName() + " ... ");

        NameParserTest test = new NameParserTest();
        test.testParseEmpty();
        test.testParseAnonymous();
        test.testParseName();

        System.out.println("OK");
    }

    private final @Service NameParser parser = NameParser.getInstance();

    private void testParseEmpty() {
        String name = parser.parse(new TokenReader(new List<>()));
        Assert.assertNull(name);
    }

    private void testParseAnonymous() {
        TokenReader reader = new TokenReader(new List<>(new SeparatorToken(",", 10)));
        String name = parser.parse(reader);
        Assert.assertNull(name);
        Assert.assertEquals(true, reader.has(",", SeparatorToken.class));
    }

    private void testParseName() {
        TokenReader reader = new TokenReader(new List<>(new WordToken("foo", 0), new SeparatorToken(",", 10)));
        String name = parser.parse(reader);
        Assert.assertEquals("foo", name);
        Assert.assertEquals(true, reader.has(",", SeparatorToken.class));
    }
}

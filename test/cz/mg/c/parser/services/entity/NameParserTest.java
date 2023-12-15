package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.constants.Anonymous;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.tokens.WordToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

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
        WordToken name = parser.parse(new TokenReader(new List<>()));
        Assert.assertSame(Anonymous.NAME, name);
    }

    private void testParseAnonymous() {
        TokenReader reader = new TokenReader(new List<>(new SeparatorToken(",", 10)));
        WordToken name = parser.parse(reader);
        Assert.assertSame(Anonymous.NAME, name);
        Assert.assertEquals(true, reader.has(",", SeparatorToken.class));
    }

    private void testParseName() {
        TokenReader reader = new TokenReader(new List<>(new WordToken("foo", 0), new SeparatorToken(",", 10)));
        WordToken name = parser.parse(reader);
        Assert.assertEquals("foo", name.getText());
        Assert.assertEquals(true, reader.has(",", SeparatorToken.class));
    }
}

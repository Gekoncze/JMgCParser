package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.tokens.SymbolToken;
import cz.mg.tokenizer.test.TokenFactory;

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
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        String name = parser.parse(new TokenReader(new List<>()));
        Assert.assertNull(name);
    }

    private void testParseAnonymous() {
        TokenReader reader = new TokenReader(new List<>(f.symbol(",")));
        String name = parser.parse(reader);
        Assert.assertNull(name);
        Assert.assertEquals(true, reader.has(",", SymbolToken.class));
    }

    private void testParseName() {
        TokenReader reader = new TokenReader(new List<>(f.word("foo"), f.symbol(",")));
        String name = parser.parse(reader);
        Assert.assertEquals("foo", name);
        Assert.assertEquals(true, reader.has(",", SymbolToken.class));
    }
}
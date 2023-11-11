package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Anonymous;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.entities.Enum;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.WordToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

public @Test class EnumTypeParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + EnumTypeParserTest.class.getSimpleName() + " ... ");

        EnumTypeParserTest test = new EnumTypeParserTest();
        test.testParseEmpty();
        test.testParseNoFields();
        test.testParseAnonymous();
        test.testParseNamed();
        test.testParseComplexConst();
        test.testParseRemainingTokens();

        System.out.println("OK");
    }

    private final @Service EnumTypeParser parser = EnumTypeParser.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseNoFields() {
        List<Token> tokens = new List<>(
            new WordToken("enum", 0),
            new WordToken("Foo", 7),
            new CurlyBrackets("", 11, new List<>())
        );
        Type type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof Enum);
        Enum enom = (Enum) type.getTypename();
        Assert.assertEquals("Foo", enom.getName().getText());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(true, enom.getEntries().isEmpty());
    }

    private void testParseAnonymous() {
        List<Token> tokens = new List<>(
            new WordToken("enum", 0),
            new CurlyBrackets("", 11, new List<>(
                new WordToken("foo", 17)
            ))
        );
        Type type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof Enum);
        Enum enom = (Enum) type.getTypename();
        Assert.assertSame(Anonymous.NAME, enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(1, enom.getEntries().count());
        Assert.assertEquals("foo", enom.getEntries().get(0).getName().getText());
        Assert.assertNull(enom.getEntries().get(0).getExpression());
    }

    private void testParseNamed() {
        List<Token> tokens = new List<>(
            new WordToken("enum", 0),
            new WordToken("FooBar", 7),
            new CurlyBrackets("", 11, new List<>(
                new WordToken("foo", 13),
                new SeparatorToken(",", 20),
                new WordToken("bar", 17)
            ))
        );
        Type type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof Enum);
        Enum enom = (Enum) type.getTypename();
        Assert.assertEquals("FooBar", enom.getName().getText());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(2, enom.getEntries().count());
    }

    private void testParseComplexConst() {
        List<Token> tokens = new List<>(
            new WordToken("const", 0),
            new WordToken("enum", 7),
            new WordToken("FooBar", 14),
            new CurlyBrackets("", 20, new List<>(
                new WordToken("foo", 25),
                new OperatorToken("=", 35),
                new WordToken("1", 30),
                new SeparatorToken(",", 32),
                new WordToken("bar", 25),
                new OperatorToken("=", 35),
                new WordToken("2", 30)

            )),
            new WordToken("const", 55),
            new OperatorToken("*", 60),
            new WordToken("const", 65)
        );
        Type type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(true, type.isConstant());
        Assert.assertEquals(1, type.getPointers().count());
        Assert.assertEquals(true, type.getPointers().getFirst().isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Enum);
        Enum enom = (Enum) type.getTypename();
        Assert.assertEquals("FooBar", enom.getName().getText());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(2, enom.getEntries().count());
        Assert.assertEquals("foo", enom.getEntries().get(0).getName().getText());
        Assert.assertNotNull(enom.getEntries().get(0).getExpression());
        Assert.assertEquals(1, enom.getEntries().get(0).getExpression().count());
        Assert.assertEquals("bar", enom.getEntries().get(1).getName().getText());
        Assert.assertNotNull(enom.getEntries().get(1).getExpression());
        Assert.assertEquals(1, enom.getEntries().get(1).getExpression().count());
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(
            new WordToken("enum", 0),
            new WordToken("Foo", 7),
            new CurlyBrackets("", 11, new List<>()),
            new WordToken("Foo2", 16)
        );
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
    }
}

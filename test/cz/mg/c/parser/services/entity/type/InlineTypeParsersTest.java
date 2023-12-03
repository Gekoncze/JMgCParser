package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Enum;
import cz.mg.c.parser.entities.Struct;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.entities.Union;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Test class InlineTypeParsersTest {
    public static void main(String[] args) {
        System.out.print("Running " + InlineTypeParsersTest.class.getSimpleName() + " ... ");

        InlineTypeParsersTest test = new InlineTypeParsersTest();
        test.testParseEmpty();
        test.testParseStruct();
        test.testParseConstStruct();
        test.testParseAnonymousStruct();
        test.testParseUnion();
        test.testParseConstUnion();
        test.testParseAnonymousUnion();
        test.testParseEnum();
        test.testParseConstEnum();
        test.testParseAnonymousEnum();
        test.testParseUnknown();

        System.out.println("OK");
    }

    private final @Service InlineTypeParsers parsers = InlineTypeParsers.getInstance();

    private void testParseEmpty() {
        Type type = parsers.parse(new TokenReader(new List<>()), false);
        Assert.assertNull(type);
    }

    private void testParseStruct() {
        List<Token> input = new List<>(
            new WordToken("struct", 0),
            new WordToken("FooBar", 10),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), false);

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Struct);
    }

    private void testParseConstStruct() {
        List<Token> input = new List<>(
            new WordToken("struct", 0),
            new WordToken("FooBar", 10),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), true);

        Assert.assertNotNull(type);
        Assert.assertEquals(true, type.isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Struct);
    }

    private void testParseAnonymousStruct() {
        List<Token> input = new List<>(
            new WordToken("struct", 0),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), false);

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Struct);
    }

    private void testParseUnion() {
        List<Token> input = new List<>(
            new WordToken("union", 0),
            new WordToken("FooBar", 10),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), false);

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Union);
    }

    private void testParseConstUnion() {
        List<Token> input = new List<>(
            new WordToken("union", 0),
            new WordToken("FooBar", 10),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), true);

        Assert.assertNotNull(type);
        Assert.assertEquals(true, type.isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Union);
    }

    private void testParseAnonymousUnion() {
        List<Token> input = new List<>(
            new WordToken("union", 0),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), false);

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Union);
    }

    private void testParseEnum() {
        List<Token> input = new List<>(
            new WordToken("enum", 0),
            new WordToken("FooBar", 10),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), false);

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Enum);
    }

    private void testParseConstEnum() {
        List<Token> input = new List<>(
            new WordToken("enum", 0),
            new WordToken("FooBar", 10),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), true);

        Assert.assertNotNull(type);
        Assert.assertEquals(true, type.isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Enum);
    }

    private void testParseAnonymousEnum() {
        List<Token> input = new List<>(
            new WordToken("enum", 0),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), false);

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Enum);
    }

    private void testParseUnknown() {
        List<Token> input = new List<>(
            new WordToken("abc", 0),
            new WordToken("FooBar", 10),
            new CurlyBrackets()
        );

        Type type = parsers.parse(new TokenReader(input), true);

        Assert.assertNull(type);
    }
}

package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.*;
import cz.mg.c.entities.types.CType;
import cz.mg.collections.set.Set;
import cz.mg.collections.set.Sets;
import cz.mg.token.tokens.brackets.CurlyBrackets;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

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
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        CType type = parsers.parse(new TokenReader(new List<>()), new Set<>());
        Assert.assertNull(type);
    }

    private void testParseStruct() {
        List<Token> input = new List<>(
            f.word("struct"),
            f.word("FooBar"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), new Set<>());

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, type.getTypename() instanceof CStruct);
    }

    private void testParseConstStruct() {
        List<Token> input = new List<>(
            f.word("struct"),
            f.word("FooBar"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), Sets.create(CModifier.CONST));

        Assert.assertNotNull(type);
        Assert.assertEquals(true, type.getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, type.getTypename() instanceof CStruct);
    }

    private void testParseAnonymousStruct() {
        List<Token> input = new List<>(
            f.word("struct"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), new Set<>());

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, type.getTypename() instanceof CStruct);
    }

    private void testParseUnion() {
        List<Token> input = new List<>(
            f.word("union"),
            f.word("FooBar"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), new Set<>());

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, type.getTypename() instanceof CUnion);
    }

    private void testParseConstUnion() {
        List<Token> input = new List<>(
            f.word("union"),
            f.word("FooBar"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), Sets.create(CModifier.CONST));

        Assert.assertNotNull(type);
        Assert.assertEquals(true, type.getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, type.getTypename() instanceof CUnion);
    }

    private void testParseAnonymousUnion() {
        List<Token> input = new List<>(
            f.word("union"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), new Set<>());

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, type.getTypename() instanceof CUnion);
    }

    private void testParseEnum() {
        List<Token> input = new List<>(
            f.word("enum"),
            f.word("FooBar"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), new Set<>());

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, type.getTypename() instanceof CEnum);
    }

    private void testParseConstEnum() {
        List<Token> input = new List<>(
            f.word("enum"),
            f.word("FooBar"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), Sets.create(CModifier.CONST));

        Assert.assertNotNull(type);
        Assert.assertEquals(true, type.getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, type.getTypename() instanceof CEnum);
    }

    private void testParseAnonymousEnum() {
        List<Token> input = new List<>(
            f.word("enum"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), new Set<>());

        Assert.assertNotNull(type);
        Assert.assertEquals(false, type.getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, type.getTypename() instanceof CEnum);
    }

    private void testParseUnknown() {
        List<Token> input = new List<>(
            f.word("abc"),
            f.word("FooBar"),
            new CurlyBrackets()
        );

        CType type = parsers.parse(new TokenReader(input), Sets.create(CModifier.CONST));

        Assert.assertNull(type);
    }
}
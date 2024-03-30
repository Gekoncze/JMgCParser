package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CPointer;
import cz.mg.c.entities.CStruct;
import cz.mg.c.entities.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class TypeParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + TypeParserTest.class.getSimpleName() + " ... ");

        TypeParserTest test = new TypeParserTest();
        test.testParseEmpty();
        test.testParseSimple();
        test.testParseSimpleConstLeft();
        test.testParseSimpleConstRight();
        test.testParseSimpleWithRemainingTokens();
        test.testParsePointersSeparate();
        test.testParsePointersTogether();
        test.testParsePointersConst();
        test.testParsePointersInvalid();
        test.testParseInlineType();

        System.out.println("OK");
    }

    private final @Service TypeParser parser = TypeParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseSimple() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("foo")
        ));

        CType type = parser.parse(reader);

        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals("foo", type.getTypename().getName());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseSimpleConstLeft() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("const"),
            f.word("foo")
        ));

        CType type = parser.parse(reader);

        Assert.assertEquals(true, type.getModifiers().isConstant());
        Assert.assertEquals("foo", type.getTypename().getName());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseSimpleConstRight() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("foo"),
            f.word("const")
        ));

        CType type = parser.parse(reader);

        Assert.assertEquals(true, type.getModifiers().isConstant());
        Assert.assertEquals("foo", type.getTypename().getName());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseSimpleWithRemainingTokens() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("foo"),
            f.word("bar")
        ));

        CType type = parser.parse(reader);

        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals("foo", type.getTypename().getName());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals("bar", reader.read().getText());
        reader.readEnd();
    }

    private void testParsePointersSeparate() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("foo"),
            f.symbol("*"),
            f.symbol("*"),
            f.symbol("*")
        ));

        CType type = parser.parse(reader);

        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals("foo", type.getTypename().getName());
        Assert.assertEquals(3, type.getPointers().count());

        for (CPointer pointer : type.getPointers()) {
            Assert.assertEquals(false, pointer.isConstant());
        }

        reader.readEnd();
    }

    private void testParsePointersTogether() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("dst"),
            f.symbol("***")
        ));

        CType type = parser.parse(reader);

        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals("dst", type.getTypename().getName());
        Assert.assertEquals(3, type.getPointers().count());

        for (CPointer pointer : type.getPointers()) {
            Assert.assertEquals(false, pointer.isConstant());
        }

        reader.readEnd();
    }

    private void testParsePointersConst() {
        testParsePointersConst(new List<>(
            f.word("const"),
            f.word("foo"),
            f.symbol("*"),
            f.symbol("*"),
            f.symbol("*"),
            f.word("const")
        ), true, false, false, true);

        testParsePointersConst(new List<>(
            f.word("foo"),
            f.word("const"),
            f.symbol("*"),
            f.symbol("*"),
            f.word("const"),
            f.symbol("*")
        ), true, false, true, false);

        testParsePointersConst(new List<>(
            f.word("foo"),
            f.symbol("*"),
            f.word("const"),
            f.symbol("*"),
            f.symbol("*")
        ), false, true, false, false);

        testParsePointersConst(new List<>(
            f.word("const"),
            f.word("foo"),
            f.word("const"),
            f.symbol("*"),
            f.word("const"),
            f.symbol("*"),
            f.word("const"),
            f.symbol("*"),
            f.word("const")
        ), true, true, true, true);

        testParsePointersConst(new List<>(
            f.word("const"),
            f.word("const"),
            f.word("const"),
            f.word("foo"),
            f.word("const"),
            f.word("const"),
            f.word("const"),
            f.symbol("*"),
            f.word("const"),
            f.word("const"),
            f.word("const"),
            f.symbol("*"),
            f.word("const"),
            f.word("const"),
            f.word("const"),
            f.symbol("*"),
            f.word("const"),
            f.word("const"),
            f.word("const")
        ), true, true, true, true);
    }

    private void testParsePointersConst(
        @Mandatory List<Token> input,
        boolean typenameConst,
        boolean... pointersConst
    ) {
        TokenReader reader = new TokenReader(input);

        CType type = parser.parse(reader);

        Assert.assertEquals(typenameConst, type.getModifiers().isConstant());
        Assert.assertEquals("foo", type.getTypename().getName());
        Assert.assertEquals(pointersConst.length, type.getPointers().count());

        int i = 0;
        for (CPointer pointer : type.getPointers()) {
            Assert.assertEquals(pointersConst[i], pointer.isConstant());
            i++;
        }

        reader.readEnd();
    }

    private void testParsePointersInvalid() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                f.word("foo"),
                f.symbol("*/")
            )));
        }).throwsException(ParseException.class);
    }

    private void testParseInlineType() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("const"),
            f.word("struct"),
            b.curlyBrackets(
                f.word("int"),
                f.word("a"),
                f.symbol(";")
            ),
            f.symbol("*")
        ));

        CType type = parser.parse(reader);

        Assert.assertEquals(1, type.getPointers().count());
        Assert.assertEquals(true, type.getModifiers().isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof CStruct);
        Assert.assertNull(type.getTypename().getName());
    }
}
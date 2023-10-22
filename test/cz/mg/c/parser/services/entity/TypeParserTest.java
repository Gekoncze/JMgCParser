package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Pointer;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NameToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;

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

        System.out.println("OK");
    }

    private final @Service TypeParser parser = TypeParser.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseSimple() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("foo", 1)
        ));

        Type type = parser.parse(reader);

        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals("foo", type.getTypename().getName().getText());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseSimpleConstLeft() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("const", 0),
            new NameToken("foo", 12)
        ));

        Type type = parser.parse(reader);

        Assert.assertEquals(true, type.isConstant());
        Assert.assertEquals("foo", type.getTypename().getName().getText());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseSimpleConstRight() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("foo", 0),
            new NameToken("const", 12)
        ));

        Type type = parser.parse(reader);

        Assert.assertEquals(true, type.isConstant());
        Assert.assertEquals("foo", type.getTypename().getName().getText());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseSimpleWithRemainingTokens() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("foo", 0),
            new NameToken("bar", 5)
        ));

        Type type = parser.parse(reader);

        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals("foo", type.getTypename().getName().getText());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals("bar", reader.read().getText());
        reader.readEnd();
    }

    private void testParsePointersSeparate() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("foo", 1),
            new OperatorToken("*", 4),
            new OperatorToken("*", 5),
            new OperatorToken("*", 6)
        ));

        Type type = parser.parse(reader);

        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals("foo", type.getTypename().getName().getText());
        Assert.assertEquals(3, type.getPointers().count());

        for (Pointer pointer : type.getPointers()) {
            Assert.assertEquals(false, pointer.isConstant());
        }

        reader.readEnd();
    }

    private void testParsePointersTogether() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("dst", 1),
            new OperatorToken("***", 4)
        ));

        Type type = parser.parse(reader);

        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals("dst", type.getTypename().getName().getText());
        Assert.assertEquals(3, type.getPointers().count());

        for (Pointer pointer : type.getPointers()) {
            Assert.assertEquals(false, pointer.isConstant());
        }

        reader.readEnd();
    }

    private void testParsePointersConst() {
        testParsePointersConst(new List<>(
            new NameToken("const", 0),
            new NameToken("foo", 0),
            new OperatorToken("*", 0),
            new OperatorToken("*", 0),
            new OperatorToken("*", 0),
            new NameToken("const", 0)
        ), true, false, false, true);

        testParsePointersConst(new List<>(
            new NameToken("foo", 0),
            new NameToken("const", 0),
            new OperatorToken("*", 0),
            new OperatorToken("*", 0),
            new NameToken("const", 0),
            new OperatorToken("*", 0)
        ), true, false, true, false);

        testParsePointersConst(new List<>(
            new NameToken("foo", 0),
            new OperatorToken("*", 0),
            new NameToken("const", 0),
            new OperatorToken("*", 0),
            new OperatorToken("*", 0)
        ), false, true, false, false);

        testParsePointersConst(new List<>(
            new NameToken("const", 0),
            new NameToken("foo", 0),
            new NameToken("const", 0),
            new OperatorToken("*", 0),
            new NameToken("const", 0),
            new OperatorToken("*", 0),
            new NameToken("const", 0),
            new OperatorToken("*", 0),
            new NameToken("const", 0)
        ), true, true, true, true);

        testParsePointersConst(new List<>(
            new NameToken("const", 0),
            new NameToken("const", 0),
            new NameToken("const", 0),
            new NameToken("foo", 0),
            new NameToken("const", 0),
            new NameToken("const", 0),
            new NameToken("const", 0),
            new OperatorToken("*", 0),
            new NameToken("const", 0),
            new NameToken("const", 0),
            new NameToken("const", 0),
            new OperatorToken("*", 0),
            new NameToken("const", 0),
            new NameToken("const", 0),
            new NameToken("const", 0),
            new OperatorToken("*", 0),
            new NameToken("const", 0),
            new NameToken("const", 0),
            new NameToken("const", 0)
        ), true, true, true, true);
    }

    private void testParsePointersConst(
        @Mandatory List<Token> input,
        boolean typenameConst,
        boolean... pointersConst
    ) {
        TokenReader reader = new TokenReader(input);

        Type type = parser.parse(reader);

        Assert.assertEquals(typenameConst, type.isConstant());
        Assert.assertEquals("foo", type.getTypename().getName().getText());
        Assert.assertEquals(pointersConst.length, type.getPointers().count());

        int i = 0;
        for (Pointer pointer : type.getPointers()) {
            Assert.assertEquals(pointersConst[i], pointer.isConstant());
            i++;
        }

        reader.readEnd();
    }

    private void testParsePointersInvalid() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                new NameToken("foo", 0),
                new OperatorToken("*/", 4)
            )));
        }).throwsException(ParseException.class);
    }
}

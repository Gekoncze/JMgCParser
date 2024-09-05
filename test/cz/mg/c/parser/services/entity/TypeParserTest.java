package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.CStruct;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.services.entity.type.TypeParser;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.c.parser.test.TypeUtils;
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

        CTypeChain typeChain = parser.parse(reader);

        Assert.assertSame(typeChain.getFirst(), typeChain.getLast());
        Assert.assertEquals(false, typeChain.getFirst().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("foo", ((CBaseType)typeChain.getFirst()).getTypename().getName());
        reader.readEnd();
    }

    private void testParseSimpleConstLeft() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("const"),
            f.word("foo")
        ));

        CTypeChain typeChain = parser.parse(reader);

        Assert.assertSame(typeChain.getFirst(), typeChain.getLast());
        Assert.assertEquals(true, typeChain.getFirst().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("foo", ((CBaseType)typeChain.getFirst()).getTypename().getName());
        reader.readEnd();
    }

    private void testParseSimpleConstRight() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("foo"),
            f.word("const")
        ));

        CTypeChain typeChain = parser.parse(reader);

        Assert.assertSame(typeChain.getFirst(), typeChain.getLast());
        Assert.assertEquals(true, typeChain.getFirst().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("foo", ((CBaseType)typeChain.getFirst()).getTypename().getName());
        reader.readEnd();
    }

    private void testParseSimpleWithRemainingTokens() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("foo"),
            f.word("bar")
        ));

        CTypeChain typeChain = parser.parse(reader);

        Assert.assertSame(typeChain.getFirst(), typeChain.getLast());
        Assert.assertEquals("foo", ((CBaseType)typeChain.getFirst()).getTypename().getName());
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

        CTypeChain typeChain = parser.parse(reader);
        List<CType> types = TypeUtils.flatten(typeChain);

        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CPointerType.class, types.get(2).getClass());
        Assert.assertEquals(CBaseType.class, types.get(3).getClass());
        Assert.assertEquals("foo", ((CBaseType)types.get(3)).getTypename().getName());

        reader.readEnd();
    }

    private void testParsePointersTogether() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("dst"),
            f.symbol("***")
        ));

        CTypeChain typeChain = parser.parse(reader);
        List<CType> types = TypeUtils.flatten(typeChain);

        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CPointerType.class, types.get(2).getClass());
        Assert.assertEquals(CBaseType.class, types.get(3).getClass());
        Assert.assertEquals("dst", ((CBaseType)types.get(3)).getTypename().getName());

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

        CTypeChain typeChain = parser.parse(reader);
        List<CType> types = TypeUtils.flatten(typeChain);

        Assert.assertEquals(pointersConst.length + 1, types.count());

        int i = 0;
        for (CType type : types) {
            if (i == pointersConst.length) {
                Assert.assertEquals(typenameConst, type.getModifiers().contains(CModifier.CONST));
                Assert.assertEquals(CBaseType.class, type.getClass());
                Assert.assertEquals("foo", ((CBaseType)type).getTypename().getName());
            } else {
                Assert.assertEquals(pointersConst[i], type.getModifiers().contains(CModifier.CONST));
            }
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
            )
        ));

        CTypeChain typeChain = parser.parse(reader);

        Assert.assertSame(typeChain.getFirst(), typeChain.getLast());
        Assert.assertEquals(true, typeChain.getFirst().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(CBaseType.class, typeChain.getFirst().getClass());
        Assert.assertEquals(CStruct.class, ((CBaseType)typeChain.getFirst()).getTypename().getClass());
        Assert.assertNull(((CBaseType)typeChain.getFirst()).getTypename().getName());
    }
}
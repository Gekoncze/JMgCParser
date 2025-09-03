package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.*;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;
import cz.mg.token.test.BracketFactory;
import cz.mg.token.test.TokenFactory;

public @Test class TypedefParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + TypedefParserTest.class.getSimpleName() + " ... ");

        TypedefParserTest test = new TypedefParserTest();
        test.testEmpty();
        test.testParseClass();
        test.testParseUnion();
        test.testParseEnum();
        test.testParseFunction();
        test.testParseNamed();
        test.testParseArray();
        test.testParseAnonymous();

        System.out.println("OK");
    }

    private final @Service TypedefParser parser = TypedefParser.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testEmpty() {
        Assertions.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseClass() {
        TokenReader reader = new TokenReader(
            new List<>(
                f.word("typedef"),
                f.word("struct"),
                b.curlyBrackets(),
                f.word("FooBar")
            )
        );

        CTypedef typedef = parser.parse(reader);
        reader.readEnd();

        List<CType> types = TypeUtils.flatten(typedef.getType());

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(1, types.count());
        Assert.assertEquals(CBaseType.class, types.get(0).getClass());
        Assert.assertEquals(CStruct.class, ((CBaseType)types.get(0)).getTypename().getClass());
    }

    private void testParseUnion() {
        TokenReader reader = new TokenReader(
            new List<>(
                f.word("typedef"),
                f.word("union"),
                b.curlyBrackets(),
                f.word("FooBar")
            )
        );

        CTypedef typedef = parser.parse(reader);
        reader.readEnd();

        List<CType> types = TypeUtils.flatten(typedef.getType());

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(1, types.count());
        Assert.assertEquals(CBaseType.class, types.get(0).getClass());
        Assert.assertEquals(CUnion.class, ((CBaseType)types.get(0)).getTypename().getClass());
    }

    private void testParseEnum() {
        TokenReader reader = new TokenReader(
            new List<>(
                f.word("typedef"),
                f.word("enum"),
                b.curlyBrackets(),
                f.word("FooBar")
            )
        );

        CTypedef typedef = parser.parse(reader);
        reader.readEnd();

        List<CType> types = TypeUtils.flatten(typedef.getType());

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(1, types.count());
        Assert.assertEquals(CBaseType.class, types.get(0).getClass());
        Assert.assertEquals(CEnum.class, ((CBaseType)types.get(0)).getTypename().getClass());
    }

    private void testParseFunction() {
        TokenReader reader = new TokenReader(
            new List<>(
                f.word("typedef"),
                f.word("void"),
                b.roundBrackets(
                    f.symbol("*"),
                    f.word("FooBar"),
                    b.squareBrackets(
                        f.number("2")
                    )
                ),
                b.roundBrackets()
            )
        );

        CTypedef typedef = parser.parse(reader);
        reader.readEnd();

        List<CType> types = TypeUtils.flatten(typedef.getType());

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(3, types.count());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        Assert.assertEquals(CFunction.class, ((CBaseType)types.get(2)).getTypename().getClass());
    }

    private void testParseNamed() {
        TokenReader reader = new TokenReader(
            new List<>(
                f.word("typedef"),
                f.word("int"),
                f.word("FooBar")
            )
        );

        CTypedef typedef = parser.parse(reader);
        reader.readEnd();

        List<CType> types = TypeUtils.flatten(typedef.getType());

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(1, types.count());
        Assert.assertEquals(CBaseType.class, types.get(0).getClass());
        Assert.assertEquals("int", ((CBaseType)types.get(0)).getTypename().getName());
    }

    private void testParseArray() {
        TokenReader reader = new TokenReader(
            new List<>(
                f.word("typedef"),
                f.word("int"),
                f.word("FooBar"),
                b.squareBrackets(
                    f.number("2")
                )
            )
        );

        CTypedef typedef = parser.parse(reader);
        reader.readEnd();

        List<CType> types = TypeUtils.flatten(typedef.getType());

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(2, types.count());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        Assert.assertEquals("int", ((CBaseType)types.get(1)).getTypename().getName());
    }

    private void testParseAnonymous() {
        TokenReader reader = new TokenReader(
            new List<>(
                f.word("typedef"),
                f.word("struct"),
                b.curlyBrackets()
            )
        );

        CTypedef typedef = parser.parse(reader);
        reader.readEnd();

        List<CType> types = TypeUtils.flatten(typedef.getType());

        Assert.assertNull(typedef.getName());
        Assert.assertEquals(1, types.count());
        Assert.assertEquals(CBaseType.class, types.get(0).getClass());
        Assert.assertEquals(CStruct.class, ((CBaseType)types.get(0)).getTypename().getClass());
    }
}

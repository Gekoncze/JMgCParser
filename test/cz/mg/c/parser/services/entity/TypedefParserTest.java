package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.*;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class TypedefParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + TypedefParserTest.class.getSimpleName() + " ... ");

        TypedefParserTest test = new TypedefParserTest();
        test.testEmpty();
        test.testParseClass();
        test.testParseUnion();
        test.testParseEnum();
        test.testParseFunction();
        test.testParseArray();
        test.testParseAnonymous();

        System.out.println("OK");
    }

    private final @Service TypedefParser parser = TypedefParser.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
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

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(CStruct.class, typedef.getType().getTypename().getClass());
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

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(CUnion.class, typedef.getType().getTypename().getClass());
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

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(CEnum.class, typedef.getType().getTypename().getClass());
    }

    private void testParseFunction() {
        TokenReader reader = new TokenReader(
            new List<>(
                f.word("typedef"),
                f.word("void"),
                b.roundBrackets(
                    f.operator("*"),
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

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(CFunction.class, typedef.getType().getTypename().getClass());
        Assert.assertEquals(1, typedef.getType().getArrays().count());
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

        Assert.assertEquals("FooBar", typedef.getName());
        Assert.assertEquals(CTypename.class, typedef.getType().getTypename().getClass());
        Assert.assertEquals(1, typedef.getType().getArrays().count());
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

        Assert.assertNull(typedef.getName());
        Assert.assertEquals(CStruct.class, typedef.getType().getTypename().getClass());
    }
}

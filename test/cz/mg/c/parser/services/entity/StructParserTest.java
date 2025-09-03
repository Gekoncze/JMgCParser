package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.CStruct;
import cz.mg.c.entities.CVariable;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;
import cz.mg.token.Token;
import cz.mg.token.test.BracketFactory;
import cz.mg.token.test.TokenFactory;

public @Test class StructParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + StructParserTest.class.getSimpleName() + " ... ");

        StructParserTest test = new StructParserTest();
        test.testEmpty();
        test.testDeclaration();
        test.testNoVariables();
        test.testAnonymous();
        test.testSingleVariable();
        test.testMultipleVariables();
        test.testInvalid();
        test.testFunctionVariable();

        System.out.println("OK");
    }

    private final @Service StructParser parser = StructParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testEmpty() {
        Assertions.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testDeclaration() {
        List<Token> input = new List<>(
            f.word("struct"),
            f.word("Foo")
        );
        CStruct struct = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", struct.getName());
        Assert.assertNull(struct.getVariables());
    }

    private void testNoVariables() {
        List<Token> input = new List<>(
            f.word("struct"),
            f.word("Foo"),
            b.curlyBrackets()
        );
        CStruct struct = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(true, struct.getVariables().isEmpty());
    }

    private void testAnonymous() {
        List<Token> input = new List<>(
            f.word("struct"),
            b.curlyBrackets()
        );
        CStruct struct = parser.parse(new TokenReader(input));
        Assert.assertNull(struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(true, struct.getVariables().isEmpty());
    }

    private void testSingleVariable() {
        List<Token> input = new List<>(
            f.word("struct"),
            f.word("Foo"),
            b.curlyBrackets(
                f.word("const"),
                f.word("int"),
                f.symbol("*"),
                f.word("bar"),
                f.symbol(";")
            )
        );

        CStruct struct = parser.parse(new TokenReader(input));

        Assert.assertEquals("Foo", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(1, struct.getVariables().count());

        CVariable variable = struct.getVariables().getFirst();
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(true, types.get(0) instanceof CPointerType);
        Assert.assertEquals(true, types.get(1).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, types.get(1) instanceof CBaseType);
        Assert.assertEquals("int", ((CBaseType)types.get(1)).getTypename().getName());
    }

    private void testMultipleVariables() {
        List<Token> input = new List<>(
            f.word("struct"),
            f.word("Bar"),
            b.curlyBrackets(
                f.word("int"),
                f.word("i"),
                f.symbol(";"),
                f.word("long"),
                f.word("l"),
                f.symbol(";"),
                f.word("short"),
                f.word("s"),
                f.symbol(";")
            )
        );

        CStruct struct = parser.parse(new TokenReader(input));

        Assert.assertEquals("Bar", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(3, struct.getVariables().count());
        Assert.assertEquals("i", struct.getVariables().get(0).getName());
        Assert.assertEquals("l", struct.getVariables().get(1).getName());
        Assert.assertEquals("s", struct.getVariables().get(2).getName());
    }

    private void testInvalid() {
        Assertions.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                f.word("struct"),
                f.word("FooBar"),
                b.curlyBrackets(
                    f.word("int"),
                    f.word("i"),
                    f.word("iii"),
                    f.symbol(";")
                )
            )));
        }).throwsException(ParseException.class);
    }

    private void testFunctionVariable() {
        // struct Foo {
        //     void (*foo)();
        // };

        List<Token> input = new List<>(
            f.word("struct"),
            f.word("Foo"),
            b.curlyBrackets(
                f.word("void"),
                b.roundBrackets(
                    f.symbol("*"),
                    f.word("foo")
                ),
                b.roundBrackets(),
                f.symbol(";")
            ),
            f.symbol(";")
        );

        CStruct struct = parser.parse(new TokenReader(input));

        Assert.assertEquals("Foo", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(1, struct.getVariables().count());

        CVariable variable = struct.getVariables().getFirst();
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("foo", variable.getName());
        Assert.assertEquals(true, types.get(0) instanceof CPointerType);
        Assert.assertEquals(true, types.get(1) instanceof CBaseType);
        Assert.assertEquals("foo", ((CBaseType)types.get(1)).getTypename().getName());
        Assert.assertEquals(true, ((CBaseType)types.get(1)).getTypename() instanceof CFunction);
    }
}
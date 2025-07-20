package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CTypename;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.collections.set.Set;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.token.test.TokenFactory;

public @Test class FunctionParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + FunctionParserTest.class.getSimpleName() + " ... ");

        FunctionParserTest test = new FunctionParserTest();
        test.testEmpty();
        test.testInterfaceNoInput();
        test.testInterfaceAnonymous();
        test.testInterfaceSingleInput();
        test.testInterfaceSingleInputArray();
        test.testInterfaceMultipleInput();
        test.testInterfaceAnonymousInput();
        test.testInterfaceAnonymousInputArray();
        test.testInterfaceInvalidInput();
        test.testFunctionEmpty();
        test.testFunction();

        System.out.println("OK");
    }

    private final @Service FunctionParser parser = FunctionParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()), createVoid());
        }).throwsException(ParseException.class);
    }

    private void testInterfaceNoInput() {
        List<Token> input = new List<>(
            f.word("foo"),
            b.roundBrackets()
        );

        CTypeChain output = createVoid();

        CFunction function = parser.parse(new TokenReader(input), output);

        Assert.assertSame(output.getFirst(), function.getOutput());
        Assert.assertEquals("foo", function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceAnonymous() {
        List<Token> input = new List<>(
            b.roundBrackets()
        );

        CTypeChain output = createVoid();

        CFunction function = parser.parse(new TokenReader(input), output);

        Assert.assertSame(output.getFirst(), function.getOutput());
        Assert.assertNull(function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceSingleInput() {
        List<Token> input = new List<>(
            f.word("constantin"),
            b.roundBrackets(
                f.word("float"),
                f.word("floating")
            )
        );

        CFunction function = parser.parse(new TokenReader(input), createVoid());

        Assert.assertEquals("constantin", function.getName());
        Assert.assertEquals(1, function.getInput().count());
        Assert.assertEquals("floating", function.getInput().getFirst().getName());
        Assert.assertEquals(CBaseType.class, function.getInput().getFirst().getType().getClass());
        Assert.assertEquals("float", ((CBaseType)function.getInput().getFirst().getType()).getTypename().getName());
        Assert.assertNull(function.getImplementation());
    }


    private void testInterfaceSingleInputArray() {
        List<Token> input = new List<>(
            f.word("constantin"),
            b.roundBrackets(
                f.word("float"),
                f.word("floating"),
                b.squareBrackets(
                    f.number("10")
                )
            )
        );

        CFunction function = parser.parse(new TokenReader(input), createVoid());

        Assert.assertEquals("constantin", function.getName());
        Assert.assertEquals(1, function.getInput().count());
        Assert.assertEquals("floating", function.getInput().getFirst().getName());

        List<CType> types = TypeUtils.flatten(function.getInput().getFirst().getType());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        Assert.assertEquals("float", ((CBaseType)types.get(1)).getTypename().getName());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceMultipleInput() {
        List<Token> input = new List<>(
            f.word("foobar"),
            b.roundBrackets(
                f.word("float"),
                f.word("floating"),
                f.symbol(","),
                f.word("double"),
                f.word("doubling"),
                f.symbol(","),
                f.word("void"),
                f.symbol("*"),
                f.symbol("*"),
                f.word("voiding")
            )
        );

        CFunction function = parser.parse(new TokenReader(input), createVoid());

        Assert.assertEquals("foobar", function.getName());
        Assert.assertEquals(3, function.getInput().count());
        Assert.assertEquals("float", ((CBaseType)function.getInput().get(0).getType()).getTypename().getName());
        Assert.assertEquals("floating", function.getInput().get(0).getName());
        Assert.assertEquals("double", ((CBaseType)function.getInput().get(1).getType()).getTypename().getName());
        Assert.assertEquals("doubling", function.getInput().get(1).getName());
        List<CType> types = TypeUtils.flatten(function.getInput().get(2).getType());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        Assert.assertEquals("void", ((CBaseType)types.get(2)).getTypename().getName());
        Assert.assertEquals("voiding", function.getInput().get(2).getName());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceAnonymousInput() {
        List<Token> input = new List<>(
            b.roundBrackets(
                f.word("float"),
                f.symbol(","),
                f.word("double"),
                f.symbol(","),
                f.word("void"),
                f.symbol("*"),
                f.symbol("*")
            )
        );

        CFunction function = parser.parse(new TokenReader(input), createVoid());

        Assert.assertNull(function.getName());
        Assert.assertEquals(3, function.getInput().count());
        Assert.assertEquals("float", ((CBaseType)function.getInput().get(0).getType()).getTypename().getName());
        Assert.assertNull(function.getInput().get(0).getName());
        Assert.assertEquals("double", ((CBaseType)function.getInput().get(1).getType()).getTypename().getName());
        Assert.assertNull(function.getInput().get(1).getName());
        List<CType> types = TypeUtils.flatten(function.getInput().get(2).getType());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        Assert.assertEquals("void", ((CBaseType)types.get(2)).getTypename().getName());
        Assert.assertNull(function.getInput().get(2).getName());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceAnonymousInputArray() {
        List<Token> input = new List<>(
            f.word("constantin"),
            b.roundBrackets(
                f.word("float"),
                b.squareBrackets(
                    f.number("10")
                )
            )
        );

        CFunction function = parser.parse(new TokenReader(input), createVoid());

        Assert.assertEquals("constantin", function.getName());
        Assert.assertEquals(1, function.getInput().count());
        Assert.assertNull(function.getInput().getFirst().getName());

        List<CType> types = TypeUtils.flatten(function.getInput().getFirst().getType());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        Assert.assertEquals(1, ((CArrayType)types.get(0)).getExpression().count());
        Assert.assertEquals("float", ((CBaseType)types.get(1)).getTypename().getName());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceInvalidInput() {
        Assert.assertThatCode(() -> {
            List<Token> input = new List<>(
                f.word("foobar"),
                b.roundBrackets(
                    f.word("float"),
                    f.word("floating"),
                    f.word("double"),
                    f.word("doubling")
                )
            );
            parser.parse(new TokenReader(input), createVoid());
        }).throwsException(ParseException.class);
    }

    private void testFunctionEmpty() {
        List<Token> input = new List<>(
            f.word("space"),
            b.roundBrackets(),
            b.curlyBrackets()
        );

        CFunction function = parser.parse(new TokenReader(input), createVoid());

        Assert.assertEquals("space", function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(true, function.getImplementation().isEmpty());
    }

    private void testFunction() {
        List<Token> input = new List<>(
            f.word("foobar"),
            b.roundBrackets(
                f.word("float"),
                f.symbol("*"),
                f.word("floating"),
                f.symbol(","),
                f.word("double"),
                f.symbol("*"),
                f.word("doubling")
            ),
            b.curlyBrackets(
                f.word("return"),
                f.word("null"),
                f.symbol(";")
            )
        );

        CFunction function = parser.parse(new TokenReader(input), createVoid());

        Assert.assertEquals("foobar", function.getName());
        Assert.assertEquals(2, function.getInput().count());
        List<CType> typesFirst = TypeUtils.flatten(function.getInput().getFirst().getType());
        List<CType> typesSecond = TypeUtils.flatten(function.getInput().getLast().getType());
        Assert.assertEquals(CPointerType.class, typesFirst.get(0).getClass());
        Assert.assertEquals(CBaseType.class, typesFirst.get(1).getClass());
        Assert.assertEquals(CPointerType.class, typesSecond.get(0).getClass());
        Assert.assertEquals(CBaseType.class, typesSecond.get(1).getClass());
        Assert.assertEquals("float", ((CBaseType)typesFirst.get(1)).getTypename().getName());
        Assert.assertEquals("floating", function.getInput().get(0).getName());
        Assert.assertEquals("double", ((CBaseType)typesSecond.get(1)).getTypename().getName());
        Assert.assertEquals("doubling", function.getInput().get(1).getName());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(3, function.getImplementation().count());
        Assert.assertEquals("return", function.getImplementation().get(0).getText());
        Assert.assertEquals("null", function.getImplementation().get(1).getText());
        Assert.assertEquals(";", function.getImplementation().get(2).getText());
    }

    private @Mandatory CTypeChain createVoid() {
        return new CTypeChain(new CBaseType(new CTypename("void"), new Set<>()));
    }
}
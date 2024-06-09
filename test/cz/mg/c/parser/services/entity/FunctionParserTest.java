package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class FunctionParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + FunctionParserTest.class.getSimpleName() + " ... ");

        FunctionParserTest test = new FunctionParserTest();
        test.testEmpty();
        test.testNoOutput();
        test.testInterfaceAnonymous();
        test.testInterfaceNoInput();
        test.testInterfaceSingleInput();
        test.testInterfaceSingleInputArray();
        test.testInterfaceMultipleInput();
        test.testInterfaceAnonymousInput();
        test.testInterfaceAnonymousInputArray();
        test.testInterfaceInvalidInput();
        test.testFunctionEmpty();
        test.testFunction();
        test.testParseWithType();

        System.out.println("OK");
    }

    private final @Service FunctionParser parser = FunctionParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testNoOutput() {
        List<Token> input = new List<>(
            f.word("void"),
            f.word("foo"),
            b.roundBrackets()
        );
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName());
        Assert.assertEquals("foo", function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceAnonymous() {
        List<Token> input = new List<>(
            f.word("void"),
            b.roundBrackets()
        );
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName());
        Assert.assertNull(function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceNoInput() {
        List<Token> input = new List<>(
            f.word("int"),
            f.symbol("*"),
            f.word("foobar"),
            b.roundBrackets()
        );
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("int", function.getOutput().getTypename().getName());
        Assert.assertEquals(1, function.getOutput().getPointers().count());
        Assert.assertEquals("foobar", function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceSingleInput() {
        List<Token> input = new List<>(
            f.word("int"),
            f.word("const"),
            f.word("constantin"),
            b.roundBrackets(
                f.word("float"),
                f.word("floating")
            )
        );
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("int", function.getOutput().getTypename().getName());
        Assert.assertEquals(true, function.getOutput().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("constantin", function.getName());
        Assert.assertEquals(1, function.getInput().count());
        Assert.assertEquals("float", function.getInput().getFirst().getType().getTypename().getName());
        Assert.assertEquals("floating", function.getInput().getFirst().getName());
        Assert.assertNull(function.getImplementation());
    }


    private void testInterfaceSingleInputArray() {
        List<Token> input = new List<>(
            f.word("int"),
            f.word("const"),
            f.word("constantin"),
            b.roundBrackets(
                f.word("float"),
                f.word("floating"),
                b.squareBrackets(
                    f.number("10")
                )
            )
        );
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("int", function.getOutput().getTypename().getName());
        Assert.assertEquals(true, function.getOutput().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("constantin", function.getName());
        Assert.assertEquals(1, function.getInput().count());
        Assert.assertEquals("float", function.getInput().getFirst().getType().getTypename().getName());
        Assert.assertEquals("floating", function.getInput().getFirst().getName());
        Assert.assertEquals(1, function.getInput().getFirst().getType().getArrays().count());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceMultipleInput() {
        List<Token> input = new List<>(
            f.word("void"),
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
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName());
        Assert.assertEquals("foobar", function.getName());
        Assert.assertEquals(3, function.getInput().count());
        Assert.assertEquals("float", function.getInput().get(0).getType().getTypename().getName());
        Assert.assertEquals("floating", function.getInput().get(0).getName());
        Assert.assertEquals("double", function.getInput().get(1).getType().getTypename().getName());
        Assert.assertEquals("doubling", function.getInput().get(1).getName());
        Assert.assertEquals("void", function.getInput().get(2).getType().getTypename().getName());
        Assert.assertEquals(2, function.getInput().get(2).getType().getPointers().count());
        Assert.assertEquals("voiding", function.getInput().get(2).getName());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceAnonymousInput() {
        List<Token> input = new List<>(
            f.word("void"),
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
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName());
        Assert.assertNull(function.getName());
        Assert.assertEquals(3, function.getInput().count());
        Assert.assertEquals("float", function.getInput().get(0).getType().getTypename().getName());
        Assert.assertNull(function.getInput().get(0).getName());
        Assert.assertEquals("double", function.getInput().get(1).getType().getTypename().getName());
        Assert.assertNull(function.getInput().get(1).getName());
        Assert.assertEquals("void", function.getInput().get(2).getType().getTypename().getName());
        Assert.assertEquals(2, function.getInput().get(2).getType().getPointers().count());
        Assert.assertNull(function.getInput().get(2).getName());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceAnonymousInputArray() {
        List<Token> input = new List<>(
            f.word("int"),
            f.word("const"),
            f.word("constantin"),
            b.roundBrackets(
                f.word("float"),
                b.squareBrackets(
                    f.number("10")
                )
            )
        );
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("int", function.getOutput().getTypename().getName());
        Assert.assertEquals(true, function.getOutput().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("constantin", function.getName());
        Assert.assertEquals(1, function.getInput().count());
        Assert.assertEquals("float", function.getInput().getFirst().getType().getTypename().getName());
        Assert.assertNull(function.getInput().getFirst().getName());
        Assert.assertEquals(1, function.getInput().getFirst().getType().getArrays().count());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceInvalidInput() {
        Assert.assertThatCode(() -> {
            List<Token> input = new List<>(
                f.word("void"),
                f.word("foobar"),
                b.roundBrackets(
                    f.word("float"),
                    f.word("floating"),
                    f.word("double"),
                    f.word("doubling")
                )
            );
            parser.parse(new TokenReader(input));
        }).throwsException(ParseException.class);
    }

    private void testFunctionEmpty() {
        List<Token> input = new List<>(
            f.word("void"),
            f.word("space"),
            b.roundBrackets(),
            b.curlyBrackets()
        );
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName());
        Assert.assertEquals("space", function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(true, function.getImplementation().isEmpty());
    }

    private void testFunction() {
        List<Token> input = new List<>(
            f.word("void"),
            f.symbol("*"),
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
        CFunction function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName());
        Assert.assertEquals(1, function.getOutput().getPointers().count());
        Assert.assertEquals("foobar", function.getName());
        Assert.assertEquals(2, function.getInput().count());
        Assert.assertEquals("float", function.getInput().get(0).getType().getTypename().getName());
        Assert.assertEquals("floating", function.getInput().get(0).getName());
        Assert.assertEquals("double", function.getInput().get(1).getType().getTypename().getName());
        Assert.assertEquals("doubling", function.getInput().get(1).getName());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(3, function.getImplementation().count());
        Assert.assertEquals("return", function.getImplementation().get(0).getText());
        Assert.assertEquals("null", function.getImplementation().get(1).getText());
        Assert.assertEquals(";", function.getImplementation().get(2).getText());
    }

    private void testParseWithType() {
        List<Token> input = new List<>(
            f.word("space"),
            b.roundBrackets(),
            b.curlyBrackets()
        );
        CType type = new CType();
        CFunction function = parser.parse(new TokenReader(input), type);
        Assert.assertSame(type, function.getOutput());
        Assert.assertEquals("space", function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(true, function.getImplementation().isEmpty());
    }
}
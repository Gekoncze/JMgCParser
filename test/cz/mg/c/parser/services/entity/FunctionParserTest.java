package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Anonymous;
import cz.mg.c.parser.entities.Function;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.c.parser.entities.brackets.SquareBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.WordToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

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

        System.out.println("OK");
    }

    private final @Service FunctionParser parser = FunctionParser.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testNoOutput() {
        List<Token> input = new List<>(
            new WordToken("void", 0),
            new WordToken("foo", 7),
            new RoundBrackets("", 8, new List<>())
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals("foo", function.getName().getText());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceAnonymous() {
        List<Token> input = new List<>(
            new WordToken("void", 0),
            new RoundBrackets("", 8, new List<>())
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName().getText());
        Assert.assertSame(Anonymous.NAME, function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceNoInput() {
        List<Token> input = new List<>(
            new WordToken("int", 0),
            new OperatorToken("*", 5),
            new WordToken("foobar", 7),
            new RoundBrackets("", 8, new List<>())
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("int", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals(1, function.getOutput().getPointers().count());
        Assert.assertEquals("foobar", function.getName().getText());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceSingleInput() {
        List<Token> input = new List<>(
            new WordToken("int", 0),
            new WordToken("const", 4),
            new WordToken("constantin", 10),
            new RoundBrackets("", 12, new List<>(
                new WordToken("float", 13),
                new WordToken("floating", 20)
            ))
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("int", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals(true, function.getOutput().isConstant());
        Assert.assertEquals("constantin", function.getName().getText());
        Assert.assertEquals(1, function.getInput().count());
        Assert.assertEquals("float", function.getInput().getFirst().getType().getTypename().getName().getText());
        Assert.assertEquals("floating", function.getInput().getFirst().getName().getText());
        Assert.assertNull(function.getImplementation());
    }


    private void testInterfaceSingleInputArray() {
        List<Token> input = new List<>(
            new WordToken("int", 0),
            new WordToken("const", 4),
            new WordToken("constantin", 10),
            new RoundBrackets("", 12, new List<>(
                new WordToken("float", 13),
                new WordToken("floating", 20),
                new SquareBrackets("", 22, new List<>(
                    new NumberToken("10", 23)
                ))
            ))
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("int", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals(true, function.getOutput().isConstant());
        Assert.assertEquals("constantin", function.getName().getText());
        Assert.assertEquals(1, function.getInput().count());
        Assert.assertEquals("float", function.getInput().getFirst().getType().getTypename().getName().getText());
        Assert.assertEquals("floating", function.getInput().getFirst().getName().getText());
        Assert.assertEquals(1, function.getInput().getFirst().getType().getArrays().count());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceMultipleInput() {
        List<Token> input = new List<>(
            new WordToken("void", 0),
            new WordToken("foobar", 10),
            new RoundBrackets("", 12, new List<>(
                new WordToken("float", 13),
                new WordToken("floating", 20),
                new SeparatorToken(",", 21),
                new WordToken("double", 28),
                new WordToken("doubling", 35),
                new SeparatorToken(",", 36),
                new WordToken("void", 44),
                new OperatorToken("*", 50),
                new OperatorToken("*", 51),
                new WordToken("voiding", 53)
            ))
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals("foobar", function.getName().getText());
        Assert.assertEquals(3, function.getInput().count());
        Assert.assertEquals("float", function.getInput().get(0).getType().getTypename().getName().getText());
        Assert.assertEquals("floating", function.getInput().get(0).getName().getText());
        Assert.assertEquals("double", function.getInput().get(1).getType().getTypename().getName().getText());
        Assert.assertEquals("doubling", function.getInput().get(1).getName().getText());
        Assert.assertEquals("void", function.getInput().get(2).getType().getTypename().getName().getText());
        Assert.assertEquals(2, function.getInput().get(2).getType().getPointers().count());
        Assert.assertEquals("voiding", function.getInput().get(2).getName().getText());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceAnonymousInput() {
        List<Token> input = new List<>(
            new WordToken("void", 0),
            new RoundBrackets("", 12, new List<>(
                new WordToken("float", 13),
                new SeparatorToken(",", 21),
                new WordToken("double", 28),
                new SeparatorToken(",", 36),
                new WordToken("void", 44),
                new OperatorToken("*", 50),
                new OperatorToken("*", 51)
            ))
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName().getText());
        Assert.assertSame(Anonymous.NAME, function.getName());
        Assert.assertEquals(3, function.getInput().count());
        Assert.assertEquals("float", function.getInput().get(0).getType().getTypename().getName().getText());
        Assert.assertSame(Anonymous.NAME, function.getInput().get(0).getName());
        Assert.assertEquals("double", function.getInput().get(1).getType().getTypename().getName().getText());
        Assert.assertSame(Anonymous.NAME, function.getInput().get(1).getName());
        Assert.assertEquals("void", function.getInput().get(2).getType().getTypename().getName().getText());
        Assert.assertEquals(2, function.getInput().get(2).getType().getPointers().count());
        Assert.assertSame(Anonymous.NAME, function.getInput().get(2).getName());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceAnonymousInputArray() {
        List<Token> input = new List<>(
            new WordToken("int", 0),
            new WordToken("const", 4),
            new WordToken("constantin", 10),
            new RoundBrackets("", 12, new List<>(
                new WordToken("float", 13),
                new SquareBrackets("", 22, new List<>(
                    new NumberToken("10", 23)
                ))
            ))
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("int", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals(true, function.getOutput().isConstant());
        Assert.assertEquals("constantin", function.getName().getText());
        Assert.assertEquals(1, function.getInput().count());
        Assert.assertEquals("float", function.getInput().getFirst().getType().getTypename().getName().getText());
        Assert.assertSame(Anonymous.NAME, function.getInput().getFirst().getName());
        Assert.assertEquals(1, function.getInput().getFirst().getType().getArrays().count());
        Assert.assertNull(function.getImplementation());
    }

    private void testInterfaceInvalidInput() {
        Assert.assertThatCode(() -> {
            List<Token> input = new List<>(
                new WordToken("void", 0),
                new WordToken("foobar", 10),
                new RoundBrackets("", 12, new List<>(
                    new WordToken("float", 13),
                    new WordToken("floating", 20),
                    new WordToken("double", 28),
                    new WordToken("doubling", 35)
                ))
            );
            parser.parse(new TokenReader(input));
        }).throwsException(ParseException.class);
    }

    private void testFunctionEmpty() {
        List<Token> input = new List<>(
            new WordToken("void", 0),
            new WordToken("space", 6),
            new RoundBrackets("", 11, new List<>()),
            new CurlyBrackets("", 13, new List<>())
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals("space", function.getName().getText());
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(true, function.getImplementation().isEmpty());
    }

    private void testFunction() {
        List<Token> input = new List<>(
            new WordToken("void", 0),
            new OperatorToken("*", 5),
            new WordToken("foobar", 10),
            new RoundBrackets("", 12, new List<>(
                new WordToken("float", 13),
                new OperatorToken("*", 19),
                new WordToken("floating", 20),
                new SeparatorToken(",", 21),
                new WordToken("double", 28),
                new OperatorToken("*", 34),
                new WordToken("doubling", 35)
            )),
            new CurlyBrackets("", 37, new List<>(
                new WordToken("return", 40),
                new WordToken("null", 47),
                new SeparatorToken(";", 52)
            ))
        );
        Function function = parser.parse(new TokenReader(input));
        Assert.assertEquals("void", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals(1, function.getOutput().getPointers().count());
        Assert.assertEquals("foobar", function.getName().getText());
        Assert.assertEquals(2, function.getInput().count());
        Assert.assertEquals("float", function.getInput().get(0).getType().getTypename().getName().getText());
        Assert.assertEquals("floating", function.getInput().get(0).getName().getText());
        Assert.assertEquals("double", function.getInput().get(1).getType().getTypename().getName().getText());
        Assert.assertEquals("doubling", function.getInput().get(1).getName().getText());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(3, function.getImplementation().count());
        Assert.assertEquals("return", function.getImplementation().get(0).getText());
        Assert.assertEquals("null", function.getImplementation().get(1).getText());
        Assert.assertEquals(";", function.getImplementation().get(2).getText());
    }
}

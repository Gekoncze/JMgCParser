package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Function;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.c.parser.entities.brackets.SquareBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.tokens.NameToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

public @Test class FunctionTypeParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + FunctionTypeParserTest.class.getSimpleName() + " ... ");

        FunctionTypeParserTest test = new FunctionTypeParserTest();
        test.testParseEmpty();
        test.testParseNoInputNoOutput();
        test.testParseMultiInputMultiPointer();
        test.testParseConstAndArray();

        System.out.println("OK");
    }

    private final @Service FunctionTypeParser parser = FunctionTypeParser.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()), new Type());
        }).throwsException(ParseException.class);
    }

    private void testParseNoInputNoOutput() {
        Type output = new Type();
        Type type = parser.parse(new TokenReader(new List<>(
            new RoundBrackets("", 0, new List<>(
                new OperatorToken("*", 1),
                new NameToken("fooptr", 2)
            )),
            new RoundBrackets("", 100, new List<>())
        )), output);
        Assert.assertEquals(1, type.getPointers().count());
        Assert.assertEquals(false, type.getPointers().getFirst().isConstant());
        Assert.assertEquals(0, type.getArrays().count());
        Assert.assertEquals(Function.class, type.getTypename().getClass());
        Function function = (Function) type.getTypename();
        Assert.assertSame(output, function.getOutput());
        Assert.assertEquals("fooptr", function.getName().getText());
        Assert.assertEquals(true, function.getInput().isEmpty());
    }

    private void testParseMultiInputMultiPointer() {
        Type output = new Type();
        Type type = parser.parse(new TokenReader(new List<>(
            new RoundBrackets("", 0, new List<>(
                new OperatorToken("**", 1),
                new NameToken("fooptrptr", 3)
            )),
            new RoundBrackets("", 12, new List<>(
                new NameToken("int", 15),
                new SeparatorToken(",", 19),
                new NameToken("int", 21)
            ))
        )), output);
        Assert.assertEquals(2, type.getPointers().count());
        Assert.assertEquals(0, type.getArrays().count());
        Assert.assertEquals(Function.class, type.getTypename().getClass());
        Function function = (Function) type.getTypename();
        Assert.assertSame(output, function.getOutput());
        Assert.assertEquals("fooptrptr", function.getName().getText());
        Assert.assertEquals(2, function.getInput().count());
    }

    private void testParseConstAndArray() {
        Type output = new Type();
        Type type = parser.parse(new TokenReader(new List<>(
            new RoundBrackets("", 0, new List<>(
                new OperatorToken("*", 1),
                new NameToken("const", 3),
                new OperatorToken("*", 10),
                new NameToken("fooptrptrarr", 12),
                new SquareBrackets("", 20, new List<>(
                    new NumberToken("3", 21)
                ))
            )),
            new RoundBrackets("", 25, new List<>(
                new NameToken("int", 28),
                new NameToken("foo", 32),
                new SeparatorToken(",", 36),
                new NameToken("int", 38),
                new NameToken("bar", 42)
            ))
        )), output);
        Assert.assertEquals(2, type.getPointers().count());
        Assert.assertEquals(true, type.getPointers().getFirst().isConstant());
        Assert.assertEquals(false, type.getPointers().getLast().isConstant());
        Assert.assertEquals(1, type.getArrays().count());
        Assert.assertEquals(1, type.getArrays().getFirst().getExpression().count());
        Assert.assertEquals("3", type.getArrays().getFirst().getExpression().getFirst().getText());
        Assert.assertEquals(Function.class, type.getTypename().getClass());
        Function function = (Function) type.getTypename();
        Assert.assertSame(output, function.getOutput());
        Assert.assertEquals("fooptrptrarr", function.getName().getText());
        Assert.assertEquals(2, function.getInput().count());
    }
}
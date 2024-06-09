package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.test.TokenFactory;

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
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()), new CType());
        }).throwsException(ParseException.class);
    }

    private void testParseNoInputNoOutput() {
        CType output = new CType();
        CType type = parser.parse(new TokenReader(new List<>(
            b.roundBrackets(
                f.symbol("*"),
                f.word("fooptr")
            ),
            b.roundBrackets()
        )), output);
        Assert.assertEquals(1, type.getPointers().count());
        Assert.assertEquals(false, type.getPointers().getFirst().isConstant());
        Assert.assertEquals(0, type.getArrays().count());
        Assert.assertEquals(CFunction.class, type.getTypename().getClass());
        CFunction function = (CFunction) type.getTypename();
        Assert.assertSame(output, function.getOutput());
        Assert.assertEquals("fooptr", function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
    }

    private void testParseMultiInputMultiPointer() {
        CType output = new CType();
        CType type = parser.parse(new TokenReader(new List<>(
            b.roundBrackets(
                f.symbol("**"),
                f.word("fooptrptr")
            ),
            b.roundBrackets(
                f.word("int"),
                f.symbol(","),
                f.word("int")
            )
        )), output);
        Assert.assertEquals(2, type.getPointers().count());
        Assert.assertEquals(0, type.getArrays().count());
        Assert.assertEquals(CFunction.class, type.getTypename().getClass());
        CFunction function = (CFunction) type.getTypename();
        Assert.assertSame(output, function.getOutput());
        Assert.assertEquals("fooptrptr", function.getName());
        Assert.assertEquals(2, function.getInput().count());
    }

    private void testParseConstAndArray() {
        CType output = new CType();
        CType type = parser.parse(new TokenReader(new List<>(
            b.roundBrackets(
                f.symbol("*"),
                f.word("const"),
                f.symbol("*"),
                f.word("fooptrptrarr"),
                b.squareBrackets(
                    f.number("3")
                )
            ),
            b.roundBrackets(
                f.word("int"),
                f.word("foo"),
                f.symbol(","),
                f.word("int"),
                f.word("bar")
            )
        )), output);
        Assert.assertEquals(2, type.getPointers().count());
        Assert.assertEquals(true, type.getPointers().getFirst().isConstant());
        Assert.assertEquals(false, type.getPointers().getLast().isConstant());
        Assert.assertEquals(1, type.getArrays().count());
        Assert.assertEquals(1, type.getArrays().getFirst().getExpression().count());
        Assert.assertEquals("3", type.getArrays().getFirst().getExpression().getFirst().getText());
        Assert.assertEquals(CFunction.class, type.getTypename().getClass());
        CFunction function = (CFunction) type.getTypename();
        Assert.assertSame(output, function.getOutput());
        Assert.assertEquals("fooptrptrarr", function.getName());
        Assert.assertEquals(2, function.getInput().count());
    }
}
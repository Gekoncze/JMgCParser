package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CStruct;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.test.TokenFactory;

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

        System.out.println("OK");
    }

    private final @Service StructParser parser = StructParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
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
                f.operator("*"),
                f.word("bar"),
                f.separator(";")
            )
        );
        CStruct struct = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(1, struct.getVariables().count());
        Assert.assertEquals("bar", struct.getVariables().getFirst().getName());
        CVariable variable = struct.getVariables().getFirst();
        Assert.assertEquals("int", variable.getType().getTypename().getName());
        Assert.assertEquals(true, variable.getType().getModifiers().isConstant());
        Assert.assertEquals(1, struct.getVariables().getFirst().getType().getPointers().count());
    }

    private void testMultipleVariables() {
        List<Token> input = new List<>(
            f.word("struct"),
            f.word("Bar"),
            b.curlyBrackets(
                f.word("int"),
                f.word("i"),
                f.separator(";"),
                f.word("long"),
                f.word("l"),
                f.separator(";"),
                f.word("short"),
                f.word("s"),
                f.separator(";")
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
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                f.word("struct"),
                f.word("FooBar"),
                b.curlyBrackets(
                    f.word("int"),
                    f.word("i"),
                    f.word("iii"),
                    f.separator(";")
                )
            )));
        }).throwsException(ParseException.class);
    }
}

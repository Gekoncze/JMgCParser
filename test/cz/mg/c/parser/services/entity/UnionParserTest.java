package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CUnion;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class UnionParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + UnionParserTest.class.getSimpleName() + " ... ");

        UnionParserTest test = new UnionParserTest();
        test.testEmpty();
        test.testDeclaration();
        test.testNoVariables();
        test.testAnonymous();
        test.testSingleVariable();
        test.testMultipleVariables();
        test.testInvalid();

        System.out.println("OK");
    }

    private final @Service UnionParser parser = UnionParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testDeclaration() {
        List<Token> input = new List<>(
            f.word("union"),
            f.word("Foo")
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", union.getName());
        Assert.assertNull(union.getVariables());
    }

    private void testNoVariables() {
        List<Token> input = new List<>(
            f.word("union"),
            f.word("Foo"),
            b.curlyBrackets()
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(true, union.getVariables().isEmpty());
    }

    private void testAnonymous() {
        List<Token> input = new List<>(
            f.word("union"),
            b.curlyBrackets()
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertNull(union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(true, union.getVariables().isEmpty());
    }

    private void testSingleVariable() {
        List<Token> input = new List<>(
            f.word("union"),
            f.word("Foo"),
            b.curlyBrackets(
                f.word("const"),
                f.word("int"),
                f.operator("*"),
                f.word("bar"),
                f.separator(";")
            )
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(1, union.getVariables().count());
        Assert.assertEquals("bar", union.getVariables().getFirst().getName());
        CVariable variable = union.getVariables().getFirst();
        Assert.assertEquals("int", variable.getType().getTypename().getName());
        Assert.assertEquals(true, variable.getType().isConstant());
        Assert.assertEquals(1, union.getVariables().getFirst().getType().getPointers().count());
    }

    private void testMultipleVariables() {
        List<Token> input = new List<>(
            f.word("union"),
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
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Bar", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(3, union.getVariables().count());
        Assert.assertEquals("i", union.getVariables().get(0).getName());
        Assert.assertEquals("l", union.getVariables().get(1).getName());
        Assert.assertEquals("s", union.getVariables().get(2).getName());
    }

    private void testInvalid() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                f.word("union"),
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

package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.CUnion;
import cz.mg.c.entities.CVariable;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.test.Assertions;
import cz.mg.token.test.BracketFactory;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.token.test.TokenFactory;

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
        Assertions.assertThatCode(() -> {
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
                f.symbol("*"),
                f.word("bar"),
                f.symbol(";")
            )
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(1, union.getVariables().count());
        Assert.assertEquals("bar", union.getVariables().getFirst().getName());
        CVariable variable = union.getVariables().getFirst();
        List<CType> variableTypes = TypeUtils.flatten(variable.getType());
        Assert.assertEquals(CPointerType.class, variableTypes.get(0).getClass());
        Assert.assertEquals(CBaseType.class, variableTypes.get(1).getClass());
        Assert.assertEquals("int", ((CBaseType)variableTypes.get(1)).getTypename().getName());
        Assert.assertEquals(true, variableTypes.get(1).getModifiers().contains(CModifier.CONST));
    }

    private void testMultipleVariables() {
        List<Token> input = new List<>(
            f.word("union"),
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
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Bar", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(3, union.getVariables().count());
        Assert.assertEquals("i", union.getVariables().get(0).getName());
        Assert.assertEquals("l", union.getVariables().get(1).getName());
        Assert.assertEquals("s", union.getVariables().get(2).getName());
    }

    private void testInvalid() {
        Assertions.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                f.word("union"),
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
}
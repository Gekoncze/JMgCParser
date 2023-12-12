package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.entities.Enum;
import cz.mg.c.parser.entities.*;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class RootEntityParsersTest {
    public static void main(String[] args) {
        System.out.print("Running " + RootEntityParsersTest.class.getSimpleName() + " ... ");

        RootEntityParsersTest test = new RootEntityParsersTest();
        test.testParseEmpty();
        test.testParseSemicolons();
        test.testParseTypedef();
        test.testParseVariable();
        test.testParseFunction();
        test.testParseStruct();
        test.testParseUnion();
        test.testParseEnum();
        test.testParseFunctionPointer();
        test.testParseMultiple();

        System.out.println("OK");
    }

    private final @Service RootEntityParsers parsers = RootEntityParsers.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testParseEmpty() {
        List<CMainEntity> entities = parsers.parse(new List<>());
        Assert.assertEquals(true, entities.isEmpty());
    }

    private void testParseSemicolons() {
        List<Token> input = new List<>(
            f.separator(";"),
            f.separator(";"),
            f.separator(";")
        );

        List<CMainEntity> entities = parsers.parse(input);
        Assert.assertEquals(true, entities.isEmpty());
    }

    private void testParseTypedef() {
        List<Token> input = new List<>(
            f.word("typedef"),
            f.word("const"),
            f.word("int"),
            f.operator("*"),
            f.word("IntPtr"),
            f.separator(";")
        );

        List<CMainEntity> entities = parsers.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof Typedef);

        Typedef typedef = (Typedef) entities.getFirst();
        Assert.assertEquals("IntPtr", typedef.getName().getText());
        Assert.assertEquals(true, typedef.getType().isConstant());
        Assert.assertEquals(true, typedef.getType().getArrays().isEmpty());
        Assert.assertEquals(1, typedef.getType().getPointers().count());
    }

    private void testParseVariable() {
        List<Token> input = new List<>(
            f.word("int"),
            f.operator("*"),
            f.operator("*"),
            f.word("foo"),
            b.squareBrackets(
                f.number("1"),
                f.operator("+"),
                f.number("1")
            ),
            f.separator(";")
        );

        List<CMainEntity> entities = parsers.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof Variable);

        Variable variable = (Variable) entities.getFirst();
        Assert.assertEquals("foo", variable.getName().getText());
        Assert.assertEquals("int", variable.getType().getTypename().getName().getText());
        Assert.assertEquals(1, variable.getType().getArrays().count());
        Assert.assertEquals(2, variable.getType().getPointers().count());
        Assert.assertEquals(3, variable.getType().getArrays().getFirst().getExpression().count());
    }

    private void testParseFunction() {
        List<Token> input = new List<>(
            f.word("void"),
            f.operator("*"),
            f.word("getAddress"),
            b.roundBrackets(
                f.word("foo"),
                f.word("bar"),
                f.separator(","),
                f.word("const"),
                f.word("int"),
                f.word("constant")
            ),
            b.curlyBrackets(
                f.word("return"),
                f.number("0"),
                f.separator(";")
            )
        );

        List<CMainEntity> entities = parsers.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof Function);

        Function function = (Function) entities.getFirst();
        Assert.assertEquals("getAddress", function.getName().getText());
        Assert.assertEquals("void", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals(1, function.getOutput().getPointers().count());
        Assert.assertEquals(true, function.getOutput().getArrays().isEmpty());
        Assert.assertEquals(false, function.getOutput().isConstant());
        Assert.assertEquals(2, function.getInput().count());
        Assert.assertEquals("bar", function.getInput().getFirst().getName().getText());
        Assert.assertEquals("foo", function.getInput().getFirst().getType().getTypename().getName().getText());
        Assert.assertEquals("constant", function.getInput().getLast().getName().getText());
        Assert.assertEquals("int", function.getInput().getLast().getType().getTypename().getName().getText());
        Assert.assertEquals(true, function.getInput().getLast().getType().isConstant());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(3, function.getImplementation().count());
    }

    private void testParseStruct() {
        List<Token> input = new List<>(
            f.word("struct"),
            f.word("FooBar"),
            b.curlyBrackets(
                f.word("int"),
                f.word("a"),
                f.separator(";"),
                f.word("int"),
                f.operator("*"),
                f.word("b"),
                f.separator(";")
            ),
            f.separator(";")
        );

        List<CMainEntity> entities = parsers.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof Struct);

        Struct struct = (Struct) entities.getFirst();
        Assert.assertEquals("FooBar", struct.getName().getText());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(2, struct.getVariables().count());
        Assert.assertEquals("a", struct.getVariables().getFirst().getName().getText());
        Assert.assertEquals("b", struct.getVariables().getLast().getName().getText());
        Assert.assertEquals("int", struct.getVariables().getFirst().getType().getTypename().getName().getText());
        Assert.assertEquals("int", struct.getVariables().getLast().getType().getTypename().getName().getText());
        Assert.assertEquals(1, struct.getVariables().getLast().getType().getPointers().count());
    }

    private void testParseUnion() {
        List<Token> input = new List<>(
            f.word("union"),
            f.word("Color"),
            b.curlyBrackets(
                f.word("int"),
                f.word("i"),
                f.separator(";"),
                f.word("char"),
                f.word("c"),
                b.squareBrackets(
                    f.number("4")
                ),
                f.separator(";"),
                f.word("float"),
                f.word("f"),
                f.separator(";")
            ),
            f.separator(";")
        );

        List<CMainEntity> entities = parsers.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof Union);

        Union union = (Union) entities.getFirst();
        Assert.assertEquals("Color", union.getName().getText());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(3, union.getVariables().count());
        Assert.assertEquals("i", union.getVariables().get(0).getName().getText());
        Assert.assertEquals("c", union.getVariables().get(1).getName().getText());
        Assert.assertEquals("f", union.getVariables().get(2).getName().getText());
        Assert.assertEquals("int", union.getVariables().get(0).getType().getTypename().getName().getText());
        Assert.assertEquals("char", union.getVariables().get(1).getType().getTypename().getName().getText());
        Assert.assertEquals("float", union.getVariables().get(2).getType().getTypename().getName().getText());
        Assert.assertEquals(1, union.getVariables().get(1).getType().getArrays().count());
        Assert.assertEquals(1, union.getVariables().get(1).getType().getArrays().getFirst().getExpression().count());
        Assert.assertEquals(
            "4",
            union.getVariables().get(1).getType().getArrays().getFirst().getExpression().getFirst().getText()
        );
    }

    private void testParseEnum() {
        List<Token> input = new List<>(
            f.word("enum"),
            f.word("Color"),
            b.curlyBrackets(
                f.word("RED"),
                f.operator("="),
                f.number("0"),
                f.separator(","),
                f.word("GREEN"),
                f.separator(","),
                f.word("BLUE"),
                f.separator(","),
                f.word("ALPHA")
            ),
            f.separator(";")
        );

        List<CMainEntity> entities = parsers.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof Enum);

        Enum enom = (Enum) entities.getFirst();
        Assert.assertEquals("Color", enom.getName().getText());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(4, enom.getEntries().count());
        Assert.assertEquals("RED", enom.getEntries().get(0).getName().getText());
        Assert.assertEquals("GREEN", enom.getEntries().get(1).getName().getText());
        Assert.assertEquals("BLUE", enom.getEntries().get(2).getName().getText());
        Assert.assertEquals("ALPHA", enom.getEntries().get(3).getName().getText());

        List<Token> expression = enom.getEntries().get(0).getExpression();
        Assert.assertNotNull(expression);
        Assert.assertNull(enom.getEntries().get(1).getExpression());
        Assert.assertNull(enom.getEntries().get(2).getExpression());
        Assert.assertNull(enom.getEntries().get(3).getExpression());
        Assert.assertEquals(1, expression.count());
        Assert.assertEquals("0", expression.getFirst().getText());
    }

    private void testParseFunctionPointer() {
        List<Token> input = new List<>(
            f.word("void"),
            b.roundBrackets(
                f.operator("*"),
                f.word("fptr")
            ),
            b.roundBrackets(),
            f.separator(";")
        );

        List<CMainEntity> entities = parsers.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof Variable);

        Variable variable = (Variable) entities.getFirst();
        Assert.assertEquals("fptr", variable.getName().getText());
        Assert.assertEquals(1, variable.getType().getPointers().count());
        Assert.assertEquals(true, variable.getType().getTypename() instanceof Function);

        Function function = (Function) variable.getType().getTypename();
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertEquals("void", function.getOutput().getTypename().getName().getText());
    }

    private void testParseMultiple() {
        List<Token> input = new List<>(
            f.word("typedef"),
            f.word("const"),
            f.word("int"),
            f.operator("*"),
            f.word("IntPtr"),
            f.separator(";"),

            f.word("void"),
            f.operator("*"),
            f.word("getAddress"),
            b.roundBrackets(
                f.word("foo"),
                f.word("bar"),
                f.separator(","),
                f.word("const"),
                f.word("int"),
                f.word("constant")
            ),
            b.curlyBrackets(
                f.word("return"),
                f.number("0"),
                f.separator(";")
            ),

            f.word("struct"),
            f.word("FooBar"),
            b.curlyBrackets(
                f.word("int"),
                f.word("a"),
                f.separator(";"),
                f.word("int"),
                f.operator("*"),
                f.word("b"),
                f.separator(";")
            ),
            f.separator(";"),

            f.word("void"),
            b.roundBrackets(
                f.operator("*"),
                f.word("fptr")
            ),
            b.roundBrackets(),
            f.separator(";")
        );

        List<CMainEntity> entities = parsers.parse(input);
        Assert.assertEquals(4, entities.count());
        Assert.assertEquals(true, entities.get(0) instanceof Typedef);
        Assert.assertEquals(true, entities.get(1) instanceof Function);
        Assert.assertEquals(true, entities.get(2) instanceof Struct);
        Assert.assertEquals(true, entities.get(3) instanceof Variable);
    }
}

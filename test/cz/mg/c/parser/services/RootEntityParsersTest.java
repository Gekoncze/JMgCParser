package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.Function;
import cz.mg.c.parser.entities.Typedef;
import cz.mg.c.parser.entities.Variable;
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
        List<Token> input = new List<>();
        List<CMainEntity> entities = parsers.parse(input);
        // TODO
    }

    private void testParseUnion() {
        List<Token> input = new List<>();
        List<CMainEntity> entities = parsers.parse(input);
        // TODO
    }

    private void testParseEnum() {
        List<Token> input = new List<>();
        List<CMainEntity> entities = parsers.parse(input);
        // TODO
    }

    private void testParseFunctionPointer() {
        List<Token> input = new List<>();
        List<CMainEntity> entities = parsers.parse(input);
        // TODO
    }

    private void testParseMultiple() {
        List<Token> input = new List<>();
        List<CMainEntity> entities = parsers.parse(input);
        // TODO
    }
}

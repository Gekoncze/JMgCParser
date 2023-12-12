package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.Typedef;
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
        List<Token> input = new List<>();
        List<CMainEntity> entities = parsers.parse(input);
        // TODO
    }

    private void testParseFunction() {
        List<Token> input = new List<>();
        List<CMainEntity> entities = parsers.parse(input);
        // TODO
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

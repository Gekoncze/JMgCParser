package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;

public @Test class RootEntityParsersTest {
    public static void main(String[] args) {
        System.out.print("Running " + RootEntityParsersTest.class.getSimpleName() + " ... ");

        RootEntityParsersTest test = new RootEntityParsersTest();
        test.testParseEmpty();
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

    private void testParseEmpty() {
        List<CMainEntity> entities = parsers.parse(new List<>());
        Assert.assertEquals(true, entities.isEmpty());
    }

    private void testParseTypedef() {
        List<Token> input = new List<>();
        List<CMainEntity> entities = parsers.parse(input);
        // TODO
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

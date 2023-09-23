package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;

public @Test class BracketParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + BracketParserTest.class.getSimpleName() + " ... ");

        BracketParserTest test = new BracketParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private final @Service BracketParser parser = BracketParser.getInstance();

    private void testParse() {
        // TODO
    }
}

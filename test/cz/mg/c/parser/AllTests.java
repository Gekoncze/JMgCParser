package cz.mg.c.parser;

import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.*;

public @Test class AllTests {
    public static void main(String[] args) {
        // cz.mg.c.parser.services
        BracketParserTest.main(args);
        CurlyBracketParserTest.main(args);
        RoundBracketParserTest.main(args);
        SquareBracketParserTest.main(args);
        StatementParserTest.main(args);
    }
}

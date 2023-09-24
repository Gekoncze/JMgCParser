package cz.mg.c.parser;

import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.BracketParserTest;
import cz.mg.c.parser.components.CurlyBracketParserTest;
import cz.mg.c.parser.components.RoundBracketParserTest;
import cz.mg.c.parser.components.SquareBracketParserTest;

public @Test class AllTests {
    public static void main(String[] args) {
        // cz.mg.c.parser.components
        BracketParserTest.main(args);
        CurlyBracketParserTest.main(args);
        RoundBracketParserTest.main(args);
        SquareBracketParserTest.main(args);
    }
}

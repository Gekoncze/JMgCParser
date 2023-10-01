package cz.mg.c.parser;

import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.BracketParserTest;
import cz.mg.c.parser.services.TypeParserTest;
import cz.mg.c.parser.services.brackets.CurlyBracketParserTest;
import cz.mg.c.parser.services.brackets.RoundBracketParserTest;
import cz.mg.c.parser.services.brackets.SquareBracketParserTest;
import cz.mg.c.parser.services.lists.BlockStatementParserTest;
import cz.mg.c.parser.services.lists.StatementParserTest;

public @Test class AllTests {
    public static void main(String[] args) {
        // cz.mg.c.parser.components
        BracketParserTest.main(args);

        // cz.mg.c.parser.services.brackets
        CurlyBracketParserTest.main(args);
        RoundBracketParserTest.main(args);
        SquareBracketParserTest.main(args);

        // cz.mg.c.parser.services.lists
        BlockStatementParserTest.main(args);
        StatementParserTest.main(args);

        // cz.mg.c.parser.services
        TypeParserTest.main(args);
    }
}

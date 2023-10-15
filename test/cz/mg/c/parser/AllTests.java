package cz.mg.c.parser;

import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.BracketParserTest;
import cz.mg.c.parser.services.ListParserTest;
import cz.mg.c.parser.services.entity.FunctionParserTest;
import cz.mg.c.parser.services.entity.StructParserTest;
import cz.mg.c.parser.services.entity.TypeParserTest;
import cz.mg.c.parser.services.bracket.CurlyBracketParserTest;
import cz.mg.c.parser.services.bracket.RoundBracketParserTest;
import cz.mg.c.parser.services.bracket.SquareBracketParserTest;
import cz.mg.c.parser.services.entity.VariableParserTest;
import cz.mg.c.parser.services.statement.BlockStatementParserTest;
import cz.mg.c.parser.services.statement.SemicolonStatementParserTest;

public @Test class AllTests {
    public static void main(String[] args) {
        // cz.mg.c.parser.components
        BracketParserTest.main(args);

        // cz.mg.c.parser.services.bracket
        CurlyBracketParserTest.main(args);
        RoundBracketParserTest.main(args);
        SquareBracketParserTest.main(args);

        // cz.mg.c.parser.services.entity
        FunctionParserTest.main(args);
        StructParserTest.main(args);
        TypeParserTest.main(args);
        VariableParserTest.main(args);

        // cz.mg.c.parser.services.statement
        BlockStatementParserTest.main(args);
        SemicolonStatementParserTest.main(args);
        // TODO - StatementParserTest

        // cz.mg.c.parser.services
        ListParserTest.main(args);
        // TODO - CMainEntityParsersTest

        // cz.mg.c.parser
        // TODO - ParserTest
    }
}

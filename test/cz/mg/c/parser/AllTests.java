package cz.mg.c.parser;

import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.services.bracket.BracketParserTest;
import cz.mg.c.parser.services.ListParserTest;
import cz.mg.c.parser.services.entity.*;
import cz.mg.c.parser.services.bracket.CurlyBracketParserTest;
import cz.mg.c.parser.services.bracket.RoundBracketParserTest;
import cz.mg.c.parser.services.bracket.SquareBracketParserTest;
import cz.mg.c.parser.services.entity.type.*;
import cz.mg.c.parser.services.statement.BlockStatementParserTest;
import cz.mg.c.parser.services.statement.SemicolonStatementParserTest;

public @Test class AllTests {
    public static void main(String[] args) {
        // cz.mg.c.parser.services.bracket
        BracketParserTest.main(args);
        CurlyBracketParserTest.main(args);
        RoundBracketParserTest.main(args);
        SquareBracketParserTest.main(args);

        // cz.mg.c.parser.services.entity.type
        ArrayParserTest.main(args);
        ConstParserTest.main(args);
        EnumTypeParserTest.main(args);
        FunctionTypeParserTest.main(args);
        InlineTypeParsersTest.main(args);
        PointerParserTest.main(args);
        StructTypeParserTest.main(args);
        UnionTypeParserTest.main(args);

        // cz.mg.c.parser.services.entity
        EnumEntryParserTest.main(args);
        EnumParserTest.main(args);
        FunctionParserTest.main(args);
        NameParserTest.main(args);
        StructParserTest.main(args);
        TypeParserTest.main(args);
        UnionParserTest.main(args);
        VariableListParserTest.main(args);
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

package cz.mg.c.parser;

import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.CTypeChainTest;
import cz.mg.c.parser.services.FileParserTest;
import cz.mg.c.parser.services.bracket.*;
import cz.mg.c.parser.services.entity.*;
import cz.mg.c.parser.services.entity.type.*;
import cz.mg.c.parser.services.list.ListParserTest;
import cz.mg.c.parser.services.list.SemicolonParserTest;

public @Test class AllTests {
    public static void main(String[] args) {
        // cz.mg.c.parser.components
        CTypeChainTest.main(args);

        // cz.mg.c.parser.services.bracket
        BracketParserTest.main(args);
        BracketParsersTest.main(args);
        CurlyBracketParserTest.main(args);
        RoundBracketParserTest.main(args);
        SquareBracketParserTest.main(args);

        // cz.mg.c.parser.services.entity.type
        ArrayTypeParserTest.main(args);
        TypeModifiersParserTest.main(args);
        FunctionTypeParserTest.main(args);
        PointerTypeParserTest.main(args);

        // cz.mg.c.parser.services.entity
        EnumEntryParserTest.main(args);
        EnumParserTest.main(args);
        FunctionParserTest.main(args);
        NameParserTest.main(args);
        StructParserTest.main(args);
        TypedefParserTest.main(args);
        TypeParserTest.main(args);
        UnionParserTest.main(args);
        VariableListParserTest.main(args);
        VariableParserTest.main(args);

        // cz.mg.c.parser.services.list
        ListParserTest.main(args);
        SemicolonParserTest.main(args);

        // cz.mg.c.parser.services
        FileParserTest.main(args);

        // cz.mg.c.parser
        CParserTest.main(args);
    }
}

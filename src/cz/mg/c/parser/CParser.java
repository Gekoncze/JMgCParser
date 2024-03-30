package cz.mg.c.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CFile;
import cz.mg.c.entities.macro.Macros;
import cz.mg.c.parser.services.RootEntityParsers;
import cz.mg.c.parser.services.bracket.BracketParsers;
import cz.mg.c.preprocessor.CPreprocessor;
import cz.mg.c.tokenizer.CTokenizer;
import cz.mg.file.File;

public @Service class CParser {
    private static volatile @Service CParser instance;

    public static @Service CParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new CParser();
                    instance.bracketParsers = BracketParsers.getInstance();
                    instance.rootEntityParsers = RootEntityParsers.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service BracketParsers bracketParsers;
    private @Service RootEntityParsers rootEntityParsers;

    private CParser() {
    }

    public @Mandatory CFile parse(@Mandatory File file, @Mandatory Macros macros) {
        return new CFile(
            file.getPath(),
            rootEntityParsers.parse(
                bracketParsers.parse(
                    new CPreprocessor(new CTokenizer(), macros).preprocess(file)
                )
            )
        );
    }
}

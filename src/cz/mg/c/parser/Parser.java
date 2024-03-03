package cz.mg.c.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CFile;
import cz.mg.c.entities.macro.Macros;
import cz.mg.c.parser.services.RootEntityParsers;
import cz.mg.c.parser.services.bracket.BracketParsers;
import cz.mg.c.preprocessor.Preprocessor;
import cz.mg.file.File;

public @Service class Parser {
    private static volatile @Service Parser instance;

    public static @Service Parser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new Parser();
                    instance.preprocessor = Preprocessor.getInstance();
                    instance.bracketParsers = BracketParsers.getInstance();
                    instance.rootEntityParsers = RootEntityParsers.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service Preprocessor preprocessor;
    private @Service BracketParsers bracketParsers;
    private @Service RootEntityParsers rootEntityParsers;

    private Parser() {
    }

    public @Mandatory CFile parse(@Mandatory File file, @Mandatory Macros macros) {
        return new CFile(
            file.getPath(),
            rootEntityParsers.parse(
                bracketParsers.parse(
                    preprocessor.preprocess(file, macros)
                )
            )
        );
    }
}

package cz.mg.c.parser;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CFile;
import cz.mg.c.entities.macro.Macros;
import cz.mg.c.parser.services.FileParser;
import cz.mg.c.parser.services.bracket.BracketParsers;
import cz.mg.c.preprocessor.CPreprocessor;
import cz.mg.file.File;

public @Component class CParser {
    private final @Mandatory BracketParsers bracketParsers = BracketParsers.getInstance();
    private final @Mandatory FileParser fileParser = FileParser.getInstance();
    private final @Mandatory CPreprocessor preprocessor;

    public CParser(@Mandatory Macros macros) {
        this(new CPreprocessor(macros));
    }

    public CParser(@Mandatory CPreprocessor preprocessor) {
        this.preprocessor = preprocessor;
    }

    public @Mandatory CFile parse(@Mandatory File file) {
        return new CFile(
            file.getPath(),
            fileParser.parse(
                bracketParsers.parse(
                    preprocessor.preprocess(file)
                )
            )
        );
    }
}

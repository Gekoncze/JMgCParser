package cz.mg.c.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.preprocessor.Preprocessor;
import cz.mg.c.preprocessor.processors.macro.entities.Macros;
import cz.mg.collections.list.List;
import cz.mg.file.File;
import cz.mg.tokenizer.entities.Token;

public @Service class Parser {
    private static volatile @Service Parser instance;

    public static @Service Parser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new Parser();
                    instance.preprocessor = Preprocessor.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service Preprocessor preprocessor;

    private Parser() {
    }

    public void parse(@Mandatory File file, @Mandatory Macros macros) {
        List<Token> tokens = preprocessor.preprocess(file, macros);
        // TODO - do the parsing
    }
}

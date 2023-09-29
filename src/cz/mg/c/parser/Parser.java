package cz.mg.c.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.services.CMainEntityParser;
import cz.mg.c.parser.services.lists.StatementParser;
import cz.mg.c.preprocessor.Preprocessor;
import cz.mg.c.preprocessor.processors.macro.entities.Macros;
import cz.mg.collections.list.List;
import cz.mg.file.File;

public @Service class Parser {
    private static volatile @Service Parser instance;

    public static @Service Parser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new Parser();
                    instance.preprocessor = Preprocessor.getInstance();
                    instance.statementParser = StatementParser.getInstance();
                    instance.cMainEntityParser = CMainEntityParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service Preprocessor preprocessor;
    private @Service StatementParser statementParser;
    private @Service CMainEntityParser cMainEntityParser;

    private Parser() {
    }

    public @Mandatory List<CMainEntity> parse(@Mandatory File file, @Mandatory Macros macros) {
        return cMainEntityParser.parse(
            statementParser.parse(
                preprocessor.preprocess(file, macros)
            )
        );
    }
}

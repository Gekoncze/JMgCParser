package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Pointer;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.OperatorToken;

public @Service class PointerParser {
    private static volatile @Service PointerParser instance;

    public static @Service PointerParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new PointerParser();
                    instance.constParser = ConstParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ConstParser constParser;

    private PointerParser() {
    }

    public @Mandatory List<Pointer> parse(@Mandatory TokenReader reader) {
        List<Pointer> pointers = new List<>();
        while (reader.has(this::pointer)) {
            Token p = reader.read();
            for (int i = 0; i < p.getText().length(); i++) {
                char ch = p.getText().charAt(i);
                if (ch == '*') {
                    pointers.addLast(new Pointer());
                } else {
                    throw new ParseException(p.getPosition(), "Unexpected character '" + ch + "' at pointer.");
                }
            }
            pointers.getLast().setConstant(constParser.parse(reader));
        }
        return pointers;
    }

    private boolean pointer(@Mandatory Token token) {
        return token instanceof OperatorToken && token.getText().startsWith("*");
    }
}

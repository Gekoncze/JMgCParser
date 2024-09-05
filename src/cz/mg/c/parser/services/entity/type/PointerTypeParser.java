package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.token.Token;
import cz.mg.token.tokens.SymbolToken;

public @Service class PointerTypeParser {
    private static volatile @Service PointerTypeParser instance;

    public static @Service PointerTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new PointerTypeParser();
                    instance.modifiersParser = ModifiersParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ModifiersParser modifiersParser;

    private PointerTypeParser() {
    }

    public @Optional CTypeChain parse(@Mandatory TokenReader reader) {
        CTypeChain pointers = null;

        while (reader.has(this::pointer)) {
            Token p = reader.read();
            for (int i = 0; i < p.getText().length(); i++) {
                char ch = p.getText().charAt(i);
                if (ch == '*') {
                    if (pointers == null) {
                        pointers = new CTypeChain(new CPointerType());
                    } else {
                        pointers.addLast(new CPointerType());
                    }
                } else {
                    throw new ParseException(p.getPosition(), "Unexpected character '" + ch + "' at pointer.");
                }
            }
            if (pointers != null) {
                pointers.getLast().getModifiers().setCollection(modifiersParser.parse(reader));
            } else {
                throw new IllegalStateException("Missing pointers to add modifiers to.");
            }
        }

        return pointers;
    }

    private boolean pointer(@Mandatory Token token) {
        return token instanceof SymbolToken && token.getText().startsWith("*");
    }
}
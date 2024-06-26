package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CModifier;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CPointer;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.SymbolToken;

public @Service class PointerParser {
    private static volatile @Service PointerParser instance;

    public static @Service PointerParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new PointerParser();
                    instance.modifiersParser = ModifiersParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ModifiersParser modifiersParser;

    private PointerParser() {
    }

    public @Mandatory List<CPointer> parse(@Mandatory TokenReader reader) {
        List<CPointer> pointers = new List<>();
        while (reader.has(this::pointer)) {
            Token p = reader.read();
            for (int i = 0; i < p.getText().length(); i++) {
                char ch = p.getText().charAt(i);
                if (ch == '*') {
                    pointers.addLast(new CPointer());
                } else {
                    throw new ParseException(p.getPosition(), "Unexpected character '" + ch + "' at pointer.");
                }
            }
            pointers.getLast().setConstant(modifiersParser.parse(reader).contains(CModifier.CONST));
        }
        return pointers;
    }

    private boolean pointer(@Mandatory Token token) {
        return token instanceof SymbolToken && token.getText().startsWith("*");
    }
}
package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.collections.pair.Pair;
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
                    instance.typeConnector = TypeConnector.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ModifiersParser modifiersParser;
    private @Service TypeConnector typeConnector;

    private PointerTypeParser() {
    }

    /**
     * Parses series of pointers. Pointers are connected into chain.
     * @return first and last pointer type object or null if there are no pointers to parse
     */
    public @Optional Pair<CPointerType, CPointerType> parse(@Mandatory TokenReader reader) {
        List<CPointerType> pointers = new List<>();

        while (reader.has(this::pointer)) {
            Token p = reader.read();
            for (int i = 0; i < p.getText().length(); i++) {
                char ch = p.getText().charAt(i);
                if (ch == '*') {
                    pointers.addLast(new CPointerType());
                } else {
                    throw new ParseException(p.getPosition(), "Unexpected character '" + ch + "' at pointer.");
                }
            }
            pointers.getLast().getModifiers().addCollectionLast(modifiersParser.parse(reader));
        }

        return typeConnector.connect(pointers);
    }

    private boolean pointer(@Mandatory Token token) {
        return token instanceof SymbolToken && token.getText().startsWith("*");
    }
}
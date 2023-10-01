package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.Pointer;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.components.TokenReader;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NameToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;

public @Service class TypeParser implements CEntityParser {
    private static volatile @Service TypeParser instance;

    public static @Service TypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                instance = new TypeParser();
            }
        }
        return instance;
    }

    @Override
    public @Mandatory Type parse(@Mandatory TokenReader reader) {
        Type type = new Type();
        type.setConstant(type.isConstant() | readConst(reader));
        type.setTypename(readTypename(reader));
        type.setConstant(type.isConstant() | readConst(reader));
        type.setPointers(readPointers(reader));
        return type;
    }

    private boolean readConst(@Mandatory TokenReader reader) {
        boolean constant = false;
        while (reader.has("const", NameToken.class)) {
            reader.read();
            constant = true;
        }
        return constant;
    }

    private @Mandatory NameToken readTypename(@Mandatory TokenReader reader) {
        return (NameToken) reader.read(NameToken.class);
    }

    private @Mandatory List<Pointer> readPointers(@Mandatory TokenReader reader) {
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
            pointers.getLast().setConstant(readConst(reader));
        }
        return pointers;
    }

    private boolean pointer(@Mandatory Token token) {
        return token instanceof OperatorToken && token.getText().startsWith("*");
    }
}

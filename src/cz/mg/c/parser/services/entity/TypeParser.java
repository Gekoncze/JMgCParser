package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.entities.Typename;
import cz.mg.c.parser.entities.Pointer;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.services.CEntityParser;
import cz.mg.collections.list.List;
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
        if (reader.has("struct", NameToken.class)) {
            return parseStructType(reader);
        } else if (reader.has("union", NameToken.class)) {
            return parseUnionType(reader);
        } else {
            Type type = parsePlainType(reader);
            if (reader.has(this::functionPointer)) {
                return parseFunctionType(reader.read(RoundBrackets.class), type);
            } else {
                return type;
            }
        }
    }

    private @Mandatory Type parsePlainType(@Mandatory TokenReader reader) {
        Type type = new Type();
        type.setConstant(type.isConstant() | readConst(reader));
        type.setTypename(new Typename(reader.read(NameToken.class)));
        type.setConstant(type.isConstant() | readConst(reader));
        type.setPointers(readPointers(reader));
        return type;
    }

    private @Mandatory Type parseFunctionType(@Mandatory RoundBrackets brackets, @Mandatory Type output) {
        throw new UnsupportedOperationException("TODO"); // TODO - implement
    }

    private @Mandatory Type parseStructType(@Mandatory TokenReader reader) {
        throw new UnsupportedOperationException("TODO"); // TODO - implement
    }

    private @Mandatory Type parseUnionType(@Mandatory TokenReader reader) {
        throw new UnsupportedOperationException("TODO"); // TODO - implement
    }

    private @Mandatory Type parseEnumType(@Mandatory TokenReader reader) {
        throw new UnsupportedOperationException("TODO"); // TODO - implement
    }

    private boolean functionPointer(@Mandatory Token token) {
        if (token instanceof RoundBrackets) {
            RoundBrackets brackets = (RoundBrackets) token;
            if (!brackets.getTokens().isEmpty()) {
                token = brackets.getTokens().getFirst();
                return token instanceof OperatorToken && token.getText().startsWith("*");
            }
        }

        return false;
    }

    private boolean readConst(@Mandatory TokenReader reader) {
        boolean constant = false;
        while (reader.has("const", NameToken.class)) {
            reader.read();
            constant = true;
        }
        return constant;
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

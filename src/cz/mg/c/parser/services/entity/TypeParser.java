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
import cz.mg.c.parser.services.entity.type.FunctionTypeParser;
import cz.mg.c.parser.services.entity.type.InlineTypeParsers;
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
                instance.inlineTypeParsers = InlineTypeParsers.getInstance();
                instance.functionTypeParser = FunctionTypeParser.getInstance();
            }
        }
        return instance;
    }

    private @Service InlineTypeParsers inlineTypeParsers;
    private @Service FunctionTypeParser functionTypeParser;

    @Override
    public @Mandatory Type parse(@Mandatory TokenReader reader) {
        Type type = inlineTypeParsers.parse(reader);
        if (type == null) {
            type = parsePlainType(reader);
            if (reader.has(functionTypeParser::matches)) {
                type = functionTypeParser.parse(reader, type);
            }
        }
        return type;
    }

    private @Mandatory Type parsePlainType(@Mandatory TokenReader reader) {
        Type type = new Type();
        type.setConstant(type.isConstant() | readConst(reader));
        type.setTypename(new Typename(reader.read(NameToken.class)));
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

package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.types.CDataType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.services.entity.StructParser;

public @Service class StructTypeParser implements InlineTypeParser {
    private static volatile @Service StructTypeParser instance;

    public static @Service StructTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new StructTypeParser();
                    instance.modifiersParser = ModifiersParser.getInstance();
                    instance.pointerTypeParser = PointerTypeParser.getInstance();
                    instance.structParser = StructParser.getInstance();
                    instance.typeConnector = TypeConnector.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ModifiersParser modifiersParser;
    private @Service PointerTypeParser pointerTypeParser;
    private @Service StructParser structParser;
    private @Service TypeConnector typeConnector;

    private StructTypeParser() {
    }

    @Override
    public @Mandatory CType parse(@Mandatory TokenReader reader) {
        CDataType dataType = new CDataType();
        dataType.getModifiers().setCollection(modifiersParser.parse(reader));
        dataType.setTypename(structParser.parse(reader));
        dataType.getModifiers().setCollection(modifiersParser.parse(reader));
        return typeConnector.connect(pointerTypeParser.parse(reader), dataType);
    }
}
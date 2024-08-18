package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.types.CDataType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.services.entity.EnumParser;

public @Service class EnumTypeParser implements InlineTypeParser {
    private static volatile @Service EnumTypeParser instance;

    public static @Service EnumTypeParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new EnumTypeParser();
                    instance.modifiersParser = ModifiersParser.getInstance();
                    instance.pointerTypeParser = PointerTypeParser.getInstance();
                    instance.enumParser = EnumParser.getInstance();
                    instance.typeConnector = TypeConnector.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ModifiersParser modifiersParser;
    private @Service PointerTypeParser pointerTypeParser;
    private @Service EnumParser enumParser;
    private @Service TypeConnector typeConnector;

    private EnumTypeParser() {
    }

    @Override
    public @Mandatory CType parse(@Mandatory TokenReader reader) {
        CDataType dataType = new CDataType();
        dataType.getModifiers().setCollection(modifiersParser.parse(reader));
        dataType.setTypename(enumParser.parse(reader));
        dataType.getModifiers().setCollection(modifiersParser.parse(reader));
        return typeConnector.connect(pointerTypeParser.parse(reader), dataType);
    }
}
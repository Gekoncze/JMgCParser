package cz.mg.c.parser.test;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;

import java.util.Objects;

public @Service class TokenValidator {
    private static volatile @Service TokenValidator instance;

    public static @Service TokenValidator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TokenValidator();
                }
            }
        }
        return instance;
    }

    private TokenValidator() {
    }

    public void assertEquals(@Optional Token expectation, @Optional Token reality) {
        Assert
            .assertThat(expectation, reality)
            .withPrintFunction(this::print)
            .withCompareFunction(this::compare)
            .areEqual();
    }

    public void assertEquals(@Optional List<Token> expectation, @Optional List<Token> reality) {
        Assert
            .assertThatCollections(expectation, reality)
            .withPrintFunction(this::print)
            .withCompareFunction(this::compare)
            .verbose("[", ",", "]")
            .areEqual();
    }

    private String print(@Mandatory Token token) {
        String fullName = token.getClass().getSimpleName();
        String shortName = fullName.substring(0, fullName.lastIndexOf("Token"));
        String text = encode(token.getText());
        return shortName + "('" + text + "'," + token.getPosition() + ")";
    }

    private boolean compare(@Mandatory Token a, @Mandatory Token b) {
        return Objects.equals(a.getClass(), b.getClass())
            && Objects.equals(a.getText(), b.getText())
            && Objects.equals(a.getPosition(), b.getPosition());
    }

    private @Mandatory String encode(@Mandatory String s) {
        return s.replaceAll("\n", "\\n").replaceAll("\t", "\\t");
    }
}

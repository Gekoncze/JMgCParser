package cz.mg.c.parser.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.tokenizer.exceptions.CodeException;

public @Error class ParseException extends CodeException {
    public ParseException(int position, @Mandatory String message) {
        super(position, message);
    }

    public ParseException(int position, @Mandatory String message, @Mandatory Exception cause) {
        super(position, message, cause);
    }
}

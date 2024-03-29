package cz.mg.c.parser.exceptions;

import cz.mg.annotations.classes.Error;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.tokenizer.exceptions.TraceableException;

public @Error class ParseException extends TraceableException {
    public ParseException(int position, @Mandatory String message) {
        super(position, message);
    }

    public ParseException(int position, @Mandatory String message, @Mandatory Exception cause) {
        super(position, message, cause);
    }
}

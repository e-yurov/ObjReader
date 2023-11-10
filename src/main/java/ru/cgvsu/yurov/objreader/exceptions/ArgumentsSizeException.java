package ru.cgvsu.yurov.objreader.exceptions;

import java.util.Locale;

public class ArgumentsSizeException extends ObjReaderException {
    public ArgumentsSizeException(ArgumentsErrorType errorType, int lineIndex) {
        super("Too " + errorType.toString().toLowerCase(Locale.ROOT) + " arguments.", lineIndex);
    }
}

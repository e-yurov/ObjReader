package ru.cgvsu.yurov.objreader.exceptions;

public class ParsingException extends ObjReaderException {
    public ParsingException(int lineIndex) {
        super("Failed to parse float value.", lineIndex);
    }
}

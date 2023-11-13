package ru.cgvsu.yurov.objreader.exceptions;

public class ParsingException extends ObjReaderException {
    public ParsingException(String type, int lineIndex) {
        super("Failed to parse " + type + " value.", lineIndex);
    }
}

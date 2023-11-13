package ru.cgvsu.yurov.objreader.exceptions;

public class FaceWordTypeException extends ObjReaderException {
    public FaceWordTypeException(int lineIndex) {
        super("Several argument types in one polygon.", lineIndex);
    }
}

package ru.cgvsu.yurov.objreader.exceptions;

public class FaceWordIndexException extends ObjReaderException {
    public FaceWordIndexException(String type, int lineIndex, int wordIndex) {
        super("Exception in face argument " + wordIndex + ": " + type + " index out of bounds.", lineIndex);
    }
}

package ru.cgvsu.yurov.objreader.exceptions;

public class ObjReaderException extends RuntimeException {
    public ObjReaderException(String errorMessage, int lineIndex) {
        super("Error parsing OBJ file on line: " + lineIndex + ". " + errorMessage);
    }
}
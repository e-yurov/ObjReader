package ru.cgvsu.yurov.objreader.exceptions;

public class TokenException extends ObjReaderException {
    public TokenException(int lineIndex) {
        super("Invalid line beginning.", lineIndex);
    }
}

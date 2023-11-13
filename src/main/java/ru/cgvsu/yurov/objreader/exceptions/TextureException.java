package ru.cgvsu.yurov.objreader.exceptions;

public class TextureException extends ObjReaderException {
    public TextureException(int lineIndex) {
        super("Texture presence mismatch.", lineIndex);
    }
}

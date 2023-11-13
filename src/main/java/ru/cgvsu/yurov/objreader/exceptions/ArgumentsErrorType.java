package ru.cgvsu.yurov.objreader.exceptions;

public enum ArgumentsErrorType {
    MANY("many"),
    FEW("few"),
    FEW_IN_POLYGON("few face"),
    FEW_IN_WORD("few face word"),
    MANY_IN_WORD("many face word");

    private final String textValue;

    ArgumentsErrorType(String textValue) {
        this.textValue = textValue;
    }

    public String getTextValue() {
        return textValue;
    }
}

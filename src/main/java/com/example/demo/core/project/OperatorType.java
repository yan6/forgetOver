package com.example.demo.core.project;

public enum OperatorType {
    plus(1), subtract(2), multiply(3), division(4);

    private final int value;

    public int getIntValue() {
        return value;
    }

    public static OperatorType valueOf(int value) {
        for (OperatorType type : OperatorType.values()) {
            if (value == type.getIntValue()) {
                return type;
            }
        }
        return plus;
    }

    OperatorType(int value) {
        this.value = value;
    }
}

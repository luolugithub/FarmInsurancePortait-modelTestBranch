package com.innovation.base;

/**
 * @author wbs on 11/2/17.
 */

public enum Model {
    BUILD(1),
    VERIFY(2);

    private int model;

    Model(int model) {
        this.model = model;
    }

    public int value() {
        return model;
    }
}

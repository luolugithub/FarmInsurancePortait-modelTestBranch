package com.innovation.exception;

public class FailedInitializeDetectorException extends Exception {

    public FailedInitializeDetectorException(){
        super("Failed load model or label file.");
    }

}

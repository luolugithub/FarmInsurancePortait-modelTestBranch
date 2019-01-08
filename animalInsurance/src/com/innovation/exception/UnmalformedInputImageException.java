package com.innovation.exception;

public class UnmalformedInputImageException extends RuntimeException {
    public UnmalformedInputImageException(){
        super("Input image must be 300 * 300 size.");
    }
}

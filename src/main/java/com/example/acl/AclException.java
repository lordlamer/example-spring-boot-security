package com.example.acl;

/**
 * Created by fhabermann on 01.03.2017.
 */
public class AclException extends Exception {
    public AclException() { super(); }
    public AclException(String message) { super(message); }
    public AclException(String message, Throwable cause) { super(message, cause); }
    public AclException(Throwable cause) { super(cause); }
}

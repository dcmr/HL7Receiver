package org.endeavourhealth.hl7receiver.model.exceptions;

public class MessageProcessingException extends Hl7ReceiverException {
    public MessageProcessingException() {
        super();
    }
    public MessageProcessingException(String message) {
        super(message);
    }
    public MessageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    public MessageProcessingException(Throwable cause) {
        super(cause);
    }
}

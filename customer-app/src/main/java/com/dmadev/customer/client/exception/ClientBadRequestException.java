package com.dmadev.customer.client.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;


//callSuper=true  - необходимость обращаться к equals, hashcode , toString -> родительского класса
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class ClientBadRequestException extends RuntimeException{
    private final List<String> errors;

    //передаю  cause- причину
    public ClientBadRequestException(Throwable cause, List<String> errors) {
        super(cause);
        this.errors = errors;
    }
}

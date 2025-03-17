package org.medical.userservice.util;

import org.medical.userservice.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class Helper<T> {


    public void isObjectNull(T t, String message) {
        if(t == null){
            throw new ResourceNotFoundException(message);
        }
    }
}
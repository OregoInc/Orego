package com.orego.corporation.orego.models.portrait.personModel.exceptions;

/**
 * NotLoadedBufferException выпрыгивает, когда буффер не был загружен из .obj файла
 */

public final class NotLoadedBufferException extends Exception{
    public NotLoadedBufferException(final String message){
        super(message);
    }
}

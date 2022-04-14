package org.titanic.cryptosx.dto.message.payload;

import org.titanic.util.JSONHelper;

/**
 * @author Hanno Skowronek
 */
public abstract class Payload {
    public String toJsonString() {
        return JSONHelper.toJsonString(this);
    }
}

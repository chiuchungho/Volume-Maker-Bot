package org.titanic.cryptosx.dto.message;

import lombok.*;
import org.titanic.util.JSONHelper;

/**
 * @author Hanno Skowronek
 */

@RequiredArgsConstructor
@Data
public class MessageFrame {
    //message 
    private final int m;
    //sequence 
    private final int i;
    //function name
    private final String n;
    //payload
    private final String o;

    public String toJsonString() {
        return JSONHelper.toJsonString(this);
    }

}

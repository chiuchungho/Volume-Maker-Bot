package org.titanic.cryptosx.dto.message.answer;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Hanno Skowronek
 */
@RequiredArgsConstructor
@Data
public class CancelOrderAnswer {
    private final boolean result;
    private final String errormsg;
    private final int errorcode;
    private final String detail;
}

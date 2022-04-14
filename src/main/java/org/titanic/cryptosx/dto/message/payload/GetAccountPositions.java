package org.titanic.cryptosx.dto.message.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * @author Chnug Ho Chiu, Hanno Skowronek
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Data
public class GetAccountPositions extends Payload {
    private final int omsId;
    private final int accountId;
}

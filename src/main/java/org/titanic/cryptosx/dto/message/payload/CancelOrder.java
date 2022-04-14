package org.titanic.cryptosx.dto.message.payload;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * @author Hanno Skowronek
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Builder
@Data
public class CancelOrder extends Payload {
    private final int OMSId;
    private final int AccountId;
    private final long ClOrderId;
    private final long OrderId;
}

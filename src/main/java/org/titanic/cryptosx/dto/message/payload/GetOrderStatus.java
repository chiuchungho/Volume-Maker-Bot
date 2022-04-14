package org.titanic.cryptosx.dto.message.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * @author Hanno Skowronek
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Data
public class GetOrderStatus extends Payload {
    private final int omsId;
    private final int accountId;
    private final long orderId;
}

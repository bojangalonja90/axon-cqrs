package com.axoncqrs.corelib.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchUserPaymentDetailsQuery {

    private String userId;

}

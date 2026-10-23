package com.portfolio.transaction.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank @Size(max = 80) String sourceAccountId,
        @NotBlank @Size(max = 80) String destinationAccountId,
        @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal amount,
        @Pattern(regexp = "^[A-Z]{3}$") String currency) { }

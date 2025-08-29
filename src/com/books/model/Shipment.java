package com.books.model;

import java.time.*;

public record Shipment(String id,
                       String originTz,
                       String destTz,
                       LocalDateTime departureLocal,
                       Duration transit) {}

package com.salesystem.product;

import java.math.BigDecimal;

public record Product(int id, String name, String category, BigDecimal price, int stock) {}

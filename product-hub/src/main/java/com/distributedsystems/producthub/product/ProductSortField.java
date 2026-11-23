package com.distributedsystems.producthub.product;

public enum ProductSortField {

    PRICE("price"),
    NAME("name"),
    CREATED_AT("createdAt");

    private final String fieldName;

    ProductSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static ProductSortField fromString(String value) {
        for (ProductSortField field : values()) {
            if (field.fieldName.equalsIgnoreCase(value)) {
                return field;
            }
        }

        throw new IllegalArgumentException(
            "Invalid sort field: '" + value
                + "'. Allowed: price, name, createdAt"
        );
    }
}
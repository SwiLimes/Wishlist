package ru.berdennikov.wishlist.exception;

public class GiftNotFoundException extends RuntimeException {
    public GiftNotFoundException(Long id) {
        super(String.format("Gift with id %d not found", id));
    }
}

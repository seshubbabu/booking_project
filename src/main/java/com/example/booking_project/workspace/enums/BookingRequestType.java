package com.example.booking_project.workspace.enums;

public enum BookingRequestType {

    PREVIOUS("previous"), UPCOMING("Upcoming"), HISTORY("History");

    private final String bookingRequestType;

    private BookingRequestType(String bookingRequestType) {
        this.bookingRequestType = bookingRequestType;
    }

    public String toString() {
        return bookingRequestType;
    }

    public static boolean contains(BookingRequestType bookingTypeInput) {
        for (BookingRequestType bookingRequestType : BookingRequestType.values()) {
            if (bookingRequestType.name().equals(bookingTypeInput.name())) {
                return true;
            }
        }
        return false;
    }

    public static String getEnumByString(String code) {
        for (BookingRequestType e : BookingRequestType.values()) {
            if (code.equals(e.toString()))
                return e.name();
        }
        return null;
    }

}

package com.example.booking_project.workspace.enums;

public enum BookingType {

    MARTIAN("Martian"), AVP_SELF("AVP Self"),AVP_BULK("AVP Bulk"), ADMIN_SELF("Admin Self"),
    ADMIN_OTHERS("Admin Others"),ADMIN_BLOCK_RELEASE("Admin Block Or Release");
    private final String bookingType;

    private BookingType(String bookingType){
        this.bookingType = bookingType;
    }

    public String toString(){
        return bookingType;
    }

    public static boolean conatins(BookingType bookingTypeInput){
        for(BookingType bookingType: BookingType.values()){
            if(bookingType.name().equals(bookingTypeInput.name())){
                return true;
            }
        }
        return false;
    }

    public static BookingType getEnumByString(String code){
        for(BookingType e: BookingType.values()){
            if(code.equals(e.toString())){
                return e;
            }
        }return null;
    }
}

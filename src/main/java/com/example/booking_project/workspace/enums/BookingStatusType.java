package com.example.booking_project.workspace.enums;

public enum BookingStatusType {

    PRE_BOOKED("PreBooked"), BOOKED("Booked"), CANCELLED("Cancelled"), AVAILABLE("AVAILABLE");

    private final String code;

    private BookingStatusType(String code){
        this.code = code;
    }

    public String toString(){
        return code;
    }

    public static String getEnumByString(String code){
        for(BookingStatusType e : BookingStatusType.values()){
            if(code.equals(e.toString())){
                return e.name();
            }
        }
        return null;
    }
}

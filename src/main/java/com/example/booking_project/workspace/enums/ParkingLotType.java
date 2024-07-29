package com.example.booking_project.workspace.enums;

public enum ParkingLotType {

    TWO_WHEELER("2W"), FOUR_WHEELER("4W"), NONE("None"), BOTH("Both");

    private final String parkingLotType;

    private ParkingLotType(String parkingLotType){
        this.parkingLotType = parkingLotType;
    }

    public String toString(){
        return parkingLotType;
    }

    public static String getEnumByString(String code){
        for(ParkingLotType e : ParkingLotType.values()){
            if(code.equals(e.toString())){
                return e.name();
            }
        }
        return null;
    }
}

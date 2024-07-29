package com.example.booking_project.workspace.enums;

public enum UserType {

    CUSTOMER("Customer"), CLIENT("Client"), EMPLOYEE("Employee");

    private final String userType;

    private UserType(String userType){
        this.userType = userType;
    }

    public String toString(){
        return userType;
    }

    public static String getEnumByString(String code){
        for(UserType e: UserType.values()){
            if(code.equals(e.toString())){
                return e.name();
            }
        }
        return null;
    }
}

package com.example.booking_project.workspace.enums;

public enum WorkspaceType {

    CUBICAL("Cubical"), CABIN("Cabin"), MANAGERIAL_CABIN("Managerial Cabin"), BOARD_ROOM("Board Room"), NOC("NOC"),
    SOC("SOC"), IOT_HUB("IOT Hub");

    private final String workspaceType;

    private WorkspaceType(String workspaceType) {
        this.workspaceType = workspaceType;
    }

    public String toString() {
        return workspaceType;
    }

    public static String getEnumByString(String code) {
        for (WorkspaceType e : WorkspaceType.values()) {
            if (code.equals(e.toString()))
                return e.name();
        }
        return null;
    }
}

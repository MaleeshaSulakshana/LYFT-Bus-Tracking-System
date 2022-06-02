package com.lyft.lyft.Model;

public class AvailableBooking {

    Integer id, vehicleId;
    String dateTime, sheets, startLocation, endLocation, busName, busRegNo, rootNo;

    public AvailableBooking(Integer id, Integer vehicleId, String dateTime, String sheets, String startLocation, String endLocation, String busName, String busRegNo, String rootNo) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.dateTime = dateTime;
        this.sheets = sheets;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.busName = busName;
        this.busRegNo = busRegNo;
        this.rootNo = rootNo;
    }

    public Integer getId() {
        return id;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getSheets() {
        return sheets;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public String getBusName() {
        return busName;
    }

    public String getBusRegNo() {
        return busRegNo;
    }

    public String getRootNo() {
        return rootNo;
    }
}

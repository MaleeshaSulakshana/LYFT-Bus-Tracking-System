package com.lyft.lyft.Model;

public class Booking {

    Integer id, user_id, available_booking_id;
    String booked_date_time, picked_location, bus_reg_no,available_date_time, start_location, end_location;

    public Booking(Integer id, Integer user_id, Integer available_booking_id, String booked_date_time, String picked_location, String bus_reg_no, String available_date_time, String start_location, String end_location) {
        this.id = id;
        this.user_id = user_id;
        this.available_booking_id = available_booking_id;
        this.booked_date_time = booked_date_time;
        this.picked_location = picked_location;
        this.bus_reg_no = bus_reg_no;
        this.available_date_time = available_date_time;
        this.start_location = start_location;
        this.end_location = end_location;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public Integer getAvailable_booking_id() {
        return available_booking_id;
    }

    public String getBooked_date_time() {
        return booked_date_time;
    }

    public String getPicked_location() {
        return picked_location;
    }

    public String getBus_reg_no() {
        return bus_reg_no;
    }

    public String getAvailable_date_time() {
        return available_date_time;
    }

    public String getStart_location() {
        return start_location;
    }

    public String getEnd_location() {
        return end_location;
    }
}

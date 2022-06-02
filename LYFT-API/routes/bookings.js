const express = require("express");
const router = express.Router();

const connection = require("../db");


// For get all
router.get("", (req, res) => {

    let details = [];
    connection.query(`SELECT id, user_id, available_booking_id, booked_date_time, picked_location FROM bookings`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For get all by user
router.get("/user/:user_id", (req, res) => {

    const { user_id } = req.params;

    let details = [];
    connection.query(`SELECT bookings.id as id, user_id, available_booking_id, booked_date_time, picked_location, vehicles.bus_reg_no, 
                        available_date_time, start_location, end_location FROM bookings
                        INNER JOIN booking_available ON booking_available.id = bookings.available_booking_id
                        INNER JOIN vehicles ON booking_available.vehicle_id = vehicles.id WHERE user_id = '${user_id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For get all by vehicle
router.get("/vehicle/:vehicle_id", (req, res) => {

    const { vehicle_id } = req.params;

    let details = [];
    connection.query(`SELECT bookings.id as id, user_id, available_booking_id, booked_date_time, picked_location, vehicles.bus_reg_no, 
                        available_date_time, start_location, end_location FROM bookings
                        INNER JOIN booking_available ON booking_available.id = bookings.available_booking_id
                        INNER JOIN vehicles ON booking_available.vehicle_id = vehicles.id WHERE vehicle_id = '${vehicle_id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For get by id
router.get("/:id", (req, res) => {

    const { id } = req.params;

    let details = [];
    connection.query(`SELECT bookings.id as id, user_id, available_booking_id, booked_date_time, picked_location, vehicles.bus_reg_no, 
        available_date_time, start_location, end_location FROM bookings
        INNER JOIN booking_available ON booking_available.id = bookings.available_booking_id
        INNER JOIN vehicles ON booking_available.vehicle_id = vehicles.id WHERE bookings.id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {
    const { user_id, available_booking_id, booked_date_time, picked_location } = req.body;

    connection.query(`SELECT COUNT(id) as bookings_count FROM bookings WHERE user_id = '${user_id}' 
                        AND available_booking_id = '${available_booking_id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        bookings_count = details[0]['bookings_count']

        if (bookings_count > 0) {
            res.json({ status: "error", msg: "Already booked!" });
        } else {

            connection.query(`INSERT INTO bookings (user_id, available_booking_id, booked_date_time, picked_location) VALUES 
                        ('${user_id}', '${available_booking_id}', '${booked_date_time}', '${picked_location}')`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "Booking not created!" });
                } else {

                    res.json({ status: "success", msg: "Booking created successfully!." });
                }

            });

        }

    });

});


module.exports = router;
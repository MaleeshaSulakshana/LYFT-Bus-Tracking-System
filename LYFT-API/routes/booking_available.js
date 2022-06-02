const express = require("express");
const router = express.Router();

const connection = require("../db");


// For get all
router.get("", (req, res) => {

    let details = [];
    connection.query(`SELECT booking_available.id as id, vehicle_id, available_date_time, (booking_available.sheet_count -(SELECT COUNT(id) FROM bookings 
    WHERE available_booking_id = booking_available.id)) as sheet_count, start_location, end_location, bus_name, bus_reg_no, root_no, price FROM booking_available 
    INNER JOIN vehicles ON vehicles.id = vehicle_id 
    WHERE booking_available.sheet_count > (SELECT COUNT(id) FROM bookings WHERE available_booking_id = booking_available.id)`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For get by id
router.get("/:id", (req, res) => {

    const { id } = req.params;

    let details = [];
    connection.query(`SELECT booking_available.id as id, vehicle_id, available_date_time, (booking_available.sheet_count -(SELECT COUNT(id) FROM bookings 
    WHERE available_booking_id = booking_available.id)) as sheet_count, start_location, end_location, bus_name, bus_reg_no, root_no, price FROM booking_available 
    INNER JOIN vehicles ON vehicles.id = vehicle_id 
    WHERE booking_available.sheet_count > (SELECT COUNT(id) FROM bookings WHERE available_booking_id = booking_available.id) AND booking_available.id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For get by vehicle and date
router.post("/vehicle", (req, res) => {

    const { id, date } = req.body;

    let details = [];
    connection.query(`SELECT booking_available.id as id, vehicle_id, available_date_time, (booking_available.sheet_count -(SELECT COUNT(id) FROM bookings 
    WHERE available_booking_id = booking_available.id)) as sheet_count, start_location, end_location, bus_name, bus_reg_no, root_no, price FROM booking_available 
    INNER JOIN vehicles ON vehicles.id = vehicle_id 
    WHERE booking_available.sheet_count > (SELECT COUNT(id) FROM bookings WHERE available_booking_id = booking_available.id) AND vehicle_id = ${id} AND available_date_time > '${date}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For get by date
router.post("/date", (req, res) => {

    const { date } = req.body;

    let details = [];
    connection.query(`SELECT booking_available.id as id, vehicle_id, available_date_time, (booking_available.sheet_count -(SELECT COUNT(id) FROM bookings 
    WHERE available_booking_id = booking_available.id)) as sheet_count, start_location, end_location, bus_name, bus_reg_no, root_no, price FROM booking_available 
    INNER JOIN vehicles ON vehicles.id = vehicle_id 
    WHERE booking_available.sheet_count > (SELECT COUNT(id) FROM bookings WHERE available_booking_id = booking_available.id) AND available_date_time > '${date}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// Filter by date
router.get("/:date_time", (req, res) => {

    const { date_time } = req.params;

    let details = [];
    connection.query(`SELECT booking_available.id as id, vehicle_id, available_date_time, (booking_available.sheet_count -(SELECT COUNT(id) FROM bookings 
    WHERE available_booking_id = booking_available.id)) as sheet_count, start_location, end_location, bus_name, bus_reg_no, root_no, price FROM booking_available 
    INNER JOIN vehicles ON vehicles.id = vehicle_id 
    WHERE booking_available.sheet_count > (SELECT COUNT(id) FROM bookings WHERE available_booking_id = booking_available.id) AND booking_available.available_date_time = '${date_time}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {
    const { vehicle_id, available_date_time, sheet_count, start_location, end_location, price } = req.body;

    connection.query(`INSERT INTO booking_available (vehicle_id, available_date_time, sheet_count, start_location, end_location, price) VALUES 
                        ('${vehicle_id}', '${available_date_time}', '${sheet_count}', '${start_location}', '${end_location}',  '${price}')`, (error, results, fields) => {
        if (error) {
            res.json({ status: "error", msg: "Booking available not created!" });
        } else {

            res.json({ status: "success", msg: "Booking available created successfully!." });
        }

    });

});


// For delete
router.delete("/:id", (req, res) => {

    const { id } = req.params;

    connection.query(`DELETE FROM booking_available WHERE id = '${id}'`, (error, results, fields) => {

        if (error) {
            res.json({ status: "error", msg: "Booking available remove failed. Try again later! " + error });
        } else {
            res.json({ status: "success", msg: "Booking available remove success!" });
        }

    });

});

module.exports = router;
const express = require("express");
const router = express.Router();

const connection = require("../db");


// For get all
router.get("", (req, res) => {

    let details = [];
    connection.query(`SELECT gps_id, latitude, longitude, vehicle, status, started_date_time FROM tracking WHERE status = 'started'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For get by id
router.get("/:id", (req, res) => {

    const { id } = req.params;

    let details = [];
    connection.query(`SELECT gps_id, latitude, longitude, vehicle, status, started_date_time, bus_reg_no, root_start, root_end FROM tracking  
                    INNER JOIN vehicles ON vehicles.id = vehicle  WHERE gps_id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {
    const { gps_id, latitude, longitude, vehicle, status, started_date_time } = req.body;

    connection.query(`INSERT INTO tracking (gps_id, latitude, longitude, vehicle, status, started_date_time) VALUES 
                        ('${gps_id}', '${latitude}', '${longitude}', '${vehicle}', '${status}', '${started_date_time}')`, (error, results, fields) => {
        if (error) {
            res.json({ status: "error", msg: "Turn not started!" });
        } else {

            res.json({ status: "success", msg: "Turn started. Now to on the root." });
        }

    });

});

// For update
router.put("/:gps_id", (req, res) => {

    const { latitude, longitude } = req.body;
    const { gps_id } = req.params;

    console.log("********* " + latitude)
    console.log("********* " + longitude)

    connection.query(`SELECT COUNT(gps_id) as track_count FROM tracking WHERE gps_id = '${gps_id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        track_count = details[0]['track_count']

        if (track_count < 1) {
            res.json({ status: "error", msg: "Profile not exist!" });
        } else {

            connection.query(`UPDATE tracking SET latitude = '${latitude}', longitude = '${longitude}' WHERE gps_id = '${gps_id}'`,
                (error, results, fields) => {
                    if (error) {
                        res.json({ status: "error", msg: "Update failed!" });
                    } else {
                        res.json({ status: "success", msg: "Updated!" });
                    }

                });

        }

    });

});

// For update psw
router.put("/end/:gps_id", (req, res) => {

    const { status } = req.body;
    const { gps_id } = req.params;

    connection.query(`SELECT COUNT(gps_id) as track_count FROM tracking 
    WHERE gps_id = '${gps_id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        track_count = details[0]['track_count']

        if (track_count < 1) {
            res.json({ status: "error", msg: "Turn not exist!" });
        } else {

            connection.query(`UPDATE tracking SET status = '${status}' WHERE gps_id = '${gps_id}'`,
                (error, results, fields) => {
                    if (error) {
                        res.json({ status: "error", msg: "Turn closed failed!" });
                    } else {
                        res.json({ status: "success", msg: "Turn closed success!" });
                    }

                });

        }

    });

});


module.exports = router;
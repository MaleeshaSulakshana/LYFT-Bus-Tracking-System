const express = require("express");
const router = express.Router();

const connection = require("../db");


// For get all
router.get("", (req, res) => {

    let details = [];
    connection.query(`SELECT id, name, email, mobile, bus_name, bus_reg_no, root_no, root_start, root_end, 
                            owner_name, owner_number, driver_name, conductor_name, image FROM vehicles`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For get by email
router.get("/:id", (req, res) => {

    const { id } = req.params;

    let details = [];
    connection.query(`SELECT id, name, email, mobile, bus_name, bus_reg_no, root_no, root_start, root_end, 
                owner_name, owner_number, driver_name, conductor_name, image FROM vehicles WHERE id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {
    const { name, email, mobile, bus_name, bus_reg_no, root_no, root_start, root_end,
        owner_name, owner_number, driver_name, conductor_name, pws, image } = req.body;

    psw = pws;


    connection.query(`SELECT COUNT(email) as vehicle_count FROM vehicles WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        vehicle_count = details[0]['vehicle_count']

        if (vehicle_count > 0) {
            res.json({ status: "error", msg: "This email already exist!" });
        } else {

            connection.query(`SELECT COUNT(bus_reg_no) as vehicle_count FROM vehicles WHERE bus_reg_no = '${bus_reg_no}'`, (error, results, fields) => {
                if (error) throw error;
                details = results;
                vehicle_count = details[0]['vehicle_count']

                if (vehicle_count > 0) {
                    res.json({ status: "error", msg: "This bus already registered!" });
                } else {

                    connection.query(`INSERT INTO vehicles (name, email, mobile, bus_name, bus_reg_no, root_no, root_start, root_end, 
                                        owner_name, owner_number, driver_name, conductor_name, psw, image) VALUES 
                                ('${name}', '${email}', '${mobile}', '${bus_name}', '${bus_reg_no}', '${root_no}', '${root_start}', '${root_end}',
                                '${owner_name}', '${owner_number}', '${driver_name}', '${conductor_name}', '${psw}', '${image}')`, (error, results, fields) => {
                        if (error) {

                            res.json({ status: "error", msg: "User not created!" });
                        } else {
                            res.json({ status: "success", msg: "Account created successfully!. Please sign in." });
                        }

                    });

                }

            });

        }

    });

});

// For update
router.put("/:id", (req, res) => {

    const { name, mobile, bus_name, bus_reg_no, root_no, root_start, root_end,
        owner_name, owner_number, driver_name, conductor_name } = req.body;
    const { id } = req.params;

    connection.query(`SELECT COUNT(email) as vehicle_count FROM vehicles WHERE id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        vehicle_count = details[0]['vehicle_count']

        if (vehicle_count < 1) {
            res.json({ status: "error", msg: "Profile not exist!" });
        } else {

            connection.query(`UPDATE vehicles SET name = '${name}', mobile = '${mobile}', bus_name = '${bus_name}', bus_reg_no = '${bus_reg_no}',
            root_no = '${root_no}', root_start = '${root_start}', root_end = '${root_end}', owner_name = '${owner_name}', 
            owner_number = '${owner_number}', driver_name = '${driver_name}', conductor_name = '${conductor_name}' WHERE id = '${id}'`,
                (error, results, fields) => {
                    if (error) {
                        res.json({ status: "error", msg: "Account update failed!" });
                    } else {
                        res.json({ status: "success", msg: "Account update success!" });
                    }

                });

        }

    });

});

// For update psw
router.put("/psw/:id", (req, res) => {

    const { psw } = req.body;
    const { id } = req.params;

    connection.query(`SELECT COUNT(email) as vehicle_count FROM vehicles 
    WHERE id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        vehicle_count = details[0]['vehicle_count']

        if (vehicle_count < 1) {
            res.json({ status: "error", msg: "User not exist!" });
        } else {

            connection.query(`UPDATE vehicles SET psw = '${psw}' WHERE id = '${id}'`,
                (error, results, fields) => {
                    if (error) {
                        res.json({ status: "error", msg: "Password updated failed!" });
                    } else {
                        res.json({ status: "success", msg: "Password update success!" });
                    }

                });

        }

    });

});


// For update image
router.put("/image/:id", (req, res) => {

    const { image } = req.body;
    const { id } = req.params;

    connection.query(`SELECT COUNT(email) as vehicle_count FROM vehicles 
    WHERE id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        vehicle_count = details[0]['vehicle_count']

        if (vehicle_count < 1) {
            res.json({ status: "error", msg: "Vehicle not exist!" });
        } else {

            connection.query(`UPDATE vehicles SET image = '${image}' WHERE id = '${id}'`,
                (error, results, fields) => {
                    if (error) {

                        console.log("******** " + error.message)
                        res.json({ status: "error", msg: "Image updated failed!" });
                    } else {
                        res.json({ status: "success", msg: "Image update success!" });
                    }

                });

        }

    });

});


// For delete
router.delete("/:id", (req, res) => {

    const { id } = req.params;

    connection.query(`DELETE FROM vehicles WHERE id = '${id}'`, (error, results, fields) => {

        if (error) {
            res.json({ status: "error", msg: "Account remove failed. Try again later! " + error });
        } else {
            res.json({ status: "success", msg: "Account remove success!" });
        }

    });

});

module.exports = router;
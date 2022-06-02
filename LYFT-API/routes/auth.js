const express = require("express");
const router = express.Router();
const connection = require("../db");

// For login
router.post("/login", (req, res) => {
    const { email, psw } = req.body;

    connection.query(`SELECT id FROM users WHERE email = '${email}' AND psw = '${psw}'`, (error, results, fields) => {
        if (error) {

            res.json({ status: "error", msg: "Some error occur.Try again!" });

        } else {

            details = results;
            user_count = details.length

            if (user_count < 1) {

                connection.query(`SELECT id FROM vehicles WHERE email = '${email}' AND psw = '${psw}'`, (error, results, fields) => {
                    if (error) {

                        res.json({ status: "error", msg: "Some error occur.Try again!" });

                    } else {

                        details = results;
                        vehicles_count = details.length

                        if (vehicles_count < 1) {
                            res.json({ status: "error", msg: "Please check your email and password!" });

                        } else {
                            res.json({ status: "success", msg: "vehicles login successfully!", user_type: "vehicles", id: details[0].id });
                        }

                    }

                });

            } else {

                res.json({ status: "success", msg: "User login successfully!", user_type: "user", id: details[0].id });
            }

        }

    });

});


module.exports = router;
const express = require("express");
const router = express.Router();

const connection = require("../db");


// For get all
router.get("", (req, res) => {

    let details = [];
    connection.query(`SELECT id, name, email, mobile FROM users`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});


// For get by email
router.get("/:id", (req, res) => {

    const { id } = req.params;

    let details = [];
    connection.query(`SELECT id, name, email, mobile FROM users WHERE id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;

        res.json(details);
    });

});

// For insert
router.post("", (req, res) => {
    const { name, email, mobile, psw } = req.body;

    connection.query(`SELECT COUNT(email) as user_count FROM users WHERE email = '${email}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        user_count = details[0]['user_count']

        if (user_count > 0) {
            res.json({ status: "error", msg: "This email already exist!" });
        } else {

            connection.query(`INSERT INTO users (name, email, mobile, psw) VALUES 
                        ('${name}', '${email}', '${mobile}', '${psw}')`, (error, results, fields) => {
                if (error) {
                    res.json({ status: "error", msg: "User not created!" });
                } else {
                    res.json({ status: "success", msg: "Account created successfully!. Please sign in." });
                }

            });

        }

    });

});

// For update
router.put("/:id", (req, res) => {

    const { name, mobile } = req.body;
    const { id } = req.params;

    connection.query(`SELECT COUNT(email) as user_count FROM users WHERE id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        user_count = details[0]['user_count']

        if (user_count < 1) {
            res.json({ status: "error", msg: "Profile not exist!" });
        } else {

            connection.query(`UPDATE users SET name = '${name}', mobile = '${mobile}' WHERE id = '${id}'`,
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

    connection.query(`SELECT COUNT(email) as user_count FROM users 
    WHERE id = '${id}'`, (error, results, fields) => {
        if (error) throw error;
        details = results;
        user_count = details[0]['user_count']

        if (user_count < 1) {
            res.json({ status: "error", msg: "User not exist!" });
        } else {

            connection.query(`UPDATE users SET psw = '${psw}' WHERE id = '${id}'`,
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

// For delete
router.delete("/:id", (req, res) => {

    const { id } = req.params;

    connection.query(`DELETE FROM users WHERE id = '${id}'`, (error, results, fields) => {

        if (error) {
            res.json({ status: "error", msg: "Account remove failed. Try again later! " + error });
        } else {
            res.json({ status: "success", msg: "Account remove success!" });
        }

    });

});

module.exports = router;
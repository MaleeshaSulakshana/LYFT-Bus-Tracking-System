
require("dotenv").config();
const cors = require("cors");
const express = require("express");

const connection = require("./db");
connection.connect();
const app = express();

const HOST_NAME = process.env.HOST_NAME || '0.0.0.0';
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// Import routes
const auth_router = require("./routes/auth");
const booking_available_router = require("./routes/booking_available");
const bookings_router = require("./routes/bookings");
const tracking_router = require("./routes/tracking");
const users_router = require("./routes/users");
const vehicles_router = require("./routes/vehicles");

// Use routes
app.use("/api/v1/auth", auth_router);
app.use("/api/v1/booking_available", booking_available_router);
app.use("/api/v1/bookings", bookings_router);
app.use("/api/v1/tracking", tracking_router);
app.use("/api/v1/users", users_router);
app.use("/api/v1/vehicles", vehicles_router);

// Server run
app.listen(PORT, HOST_NAME, () => {
    console.log(`server is running ${HOST_NAME}:${PORT}`);
});
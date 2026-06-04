import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axiosConfig";

function Register() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
    });

    function handleChange(e) {
        setFormData({
        ...formData,
        [e.target.name]: e.target.value,
        });
    }

    async function handleRegister(e) {
        e.preventDefault();

        try {
        await api.post("/auth/register", {
            firstName: formData.firstName,
            lastName: formData.lastName,
            email: formData.email,
            password: formData.password,
        });

        alert("Registration successful");
        navigate("/login");
        } catch (error) {
        alert("Registration failed");
        console.log(error);
        }
    }

    return (
        <div className="page">
        <h1>Register</h1>

        <form onSubmit={handleRegister}>
            <input name="firstName" placeholder="First name" value={formData.firstName} onChange={handleChange} required />
            <input name="lastName" placeholder="Last name" value={formData.lastName} onChange={handleChange} required />
            <input name="email" type="email" placeholder="Email" value={formData.email} onChange={handleChange} required />
            <input name="password" type="password" placeholder="Password" value={formData.password} onChange={handleChange} required />

            <button type="submit">Register</button>
        </form>

        <Link to="/login">Go to Login</Link>
        </div>
    );
}

export default Register;
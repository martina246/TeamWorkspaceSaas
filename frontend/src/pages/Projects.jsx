import { useEffect, useState } from "react";
import api from "../api/axiosConfig";

function Projects() {
    const [projects, setProjects] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        description: "",
        status: "",
    });

    async function fetchProjects() {
        const response = await api.get("/projects");
        setProjects(response.data);
    }

    useEffect(() => {
        fetchProjects();
    }, []);

    function handleChange(e) {
        setFormData({
        ...formData,
        [e.target.name]: e.target.value,
        });
    }

    async function handleCreateProject(e) {
        e.preventDefault();

        try {
        await api.post("/projects", formData);

        setFormData({
            name: "",
            description: "",
            status: "",
        });

        await fetchProjects();
        } catch (error) {
        alert("Project creation failed");
        console.log(error);
        }
    }

    return (
        <div className="page">
        <h1>Projects</h1>

        <form onSubmit={handleCreateProject}>
            <input name="name" placeholder="Project name" value={formData.name} onChange={handleChange} required />
            <input name="description" placeholder="Description" value={formData.description} onChange={handleChange} required />
            <input name="status" placeholder="Status" value={formData.status} onChange={handleChange} required />
            <button type="submit">Create Project</button>
        </form>

        <div className="list">
            {projects.map((project) => (
            <div className="card" key={project.id}>
                <h3>{project.name}</h3>
                <p>{project.description}</p>
                <p>Status: {project.status}</p>
                <p>Organization: {project.organizationName}</p>
            </div>
            ))}
        </div>
        </div>
    );
}

export default Projects;
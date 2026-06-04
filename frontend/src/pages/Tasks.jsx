import { useEffect, useState } from "react";
import api from "../api/axiosConfig";

function Tasks({ user }) {
    const [tasks, setTasks] = useState([]);
    const [projects, setProjects] = useState([]);
    const [users, setUsers] = useState([]);

    const isAdmin = user?.role === "ADMIN" || user?.role === "SUPERADMIN";

    const [formData, setFormData] = useState({
        title: "",
        description: "",
        priority: "",
        status: "",
        dueDate: "",
        assignedUserId: "",
        projectId: "",
    });

    async function fetchTasks() {
        const response = await api.get("/tasks");
        setTasks(response.data);
    }

    useEffect(() => {
        fetchTasks();

        if (isAdmin) {
            fetchProjects();
            fetchUsers();
        }
    }, [isAdmin]);

    function handleChange(e) {
        setFormData({
        ...formData,
        [e.target.name]: e.target.value,
        });
    }

    async function fetchProjects() {
        const response = await api.get("/projects");
        setProjects(response.data);
    }

    async function fetchUsers() {
        const response = await api.get("/users");
        setUsers(response.data);
    }

    async function handleCreateTask(e) {
        e.preventDefault();

        try {
        await api.post("/tasks", {
            title: formData.title,
            description: formData.description,
            priority: formData.priority,
            status: formData.status,
            dueDate: formData.dueDate,
            assignedUserId: Number(formData.assignedUserId),
            projectId: Number(formData.projectId),
        });

        setFormData({
            title: "",
            description: "",
            priority: "",
            status: "",
            dueDate: "",
            assignedUserId: "",
            projectId: "",
        });

        await fetchTasks();
        } catch (error) {
        alert("Task creation failed");
        console.log(error);
        }
    }

    return (
        <div className="page">
        <h1>Tasks</h1>

        {isAdmin && (<form onSubmit={handleCreateTask}>
            <input name="title" placeholder="Title" value={formData.title} onChange={handleChange} required />
            <input name="description" placeholder="Description" value={formData.description} onChange={handleChange} required />
            <input name="priority" placeholder="Priority" value={formData.priority} onChange={handleChange} required />
            <input name="status" placeholder="Status" value={formData.status} onChange={handleChange} required />
            <input name="dueDate" type="date" value={formData.dueDate} onChange={handleChange} required />

            <select name="assignedUserId" value={formData.assignedUserId} onChange={handleChange} required>
                <option value="">Select user</option>

                {users.map((user) => (
                    <option key={user.id} value={user.id}>
                        {user.email}
                    </option>
                ))}
            </select>

            <select name="projectId" value={formData.projectId} onChange={handleChange} required>
                <option value="">Select project</option>

                {projects.map((project) => (
                    <option key={project.id} value={project.id}>
                        {project.name}
                    </option>
                ))}
            </select>

            <button type="submit">Create Task</button>
        </form>)}

        <div className="list">
            {tasks.map((task) => (
            <div className="card" key={task.id}>
                <h3>{task.title}</h3>
                <p>{task.description}</p>
                <p>Priority: {task.priority}</p>
                <p>Status: {task.status}</p>
                <p>Due date: {task.dueDate}</p>
                <p>Assigned user: {task.assignedUserEmail}</p>
                <p>Project: {task.projectName}</p>
            </div>
            ))}
        </div>
        </div>
    );
}

export default Tasks;
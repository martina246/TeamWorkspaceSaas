import { useEffect, useState } from "react";
import api from "../api/axiosConfig";

function ActivityLogs() {
    const [logs, setLogs] = useState([]);

    async function fetchLogs() {
        const response = await api.get("/activity-logs");
        setLogs(response.data);
    }

    useEffect(() => {
        fetchLogs();
    }, []);

    return (
        <div className="page">
        <h1>Activity Logs</h1>

        <div className="list">
            {logs.map((log) => (
            <div className="card" key={log.id}>
                <h3>{log.action}</h3>
                <p>{log.description}</p>
                <p>User: {log.userEmail}</p>
                <p>Organization: {log.organizationName}</p>
                <p>Created at: {log.createdAt}</p>
            </div>
            ))}
        </div>
        </div>
    );
}

export default ActivityLogs;
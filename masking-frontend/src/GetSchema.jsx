import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchSchema } from "../api/fetchSchema";

function GetSchema() {
  const [dbUrl, setDbUrl] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const getSchema = async () => {
    setError("");
    try {
      const schema = await fetchSchema(dbUrl, username, password);
      navigate("/maskSchema", { state: { schema } });
    } catch (err) {
      setError("Failed to fetch schema. Check credentials or server.");
    }
  };

  return (
    <div style={{ maxWidth: "600px", margin: "auto", padding: "20px" }}>
      <h2>Database Schema Viewer</h2>

      <input
        type="text"
        placeholder="Database URL"
        value={dbUrl}
        onChange={(e) => setDbUrl(e.target.value)}
        style={{ width: "100%", marginBottom: "10px", padding: "8px" }}
      />
      <input
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        style={{ width: "100%", marginBottom: "10px", padding: "8px" }}
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        style={{ width: "100%", marginBottom: "10px", padding: "8px" }}
      />
      <button onClick={getSchema} style={{ padding: "10px", width: "100%" }}>
        Fetch Schema
      </button>

      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}

export default GetSchema;
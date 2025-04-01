import { useState } from "react";
import { fetchSchema } from "../api/fetchSchema";

function App() {
  const [dbUrl, setDbUrl] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [schema, setSchema] = useState(null);
  const [error, setError] = useState("");

  const getSchema = async () => {
    
    setError("");
    setSchema(null);
    fetchSchema({dbUrl, username, password}, setError, setSchema);

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

      {schema && (
        <div style={{ marginTop: "20px", border: "1px solid #ddd", padding: "10px" }}>
          <h3>Schema Details</h3>
          <pre style={{ whiteSpace: "pre-wrap", wordWrap: "break-word" }}>
            {JSON.stringify(schema, null, 2)}
          </pre>
        </div>
      )}
    </div>
  );
}

export default App;

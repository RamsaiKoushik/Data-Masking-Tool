import React from "react";
import { useNavigate } from "react-router-dom";

function HomePage() {
  const navigate = useNavigate();

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h1>Welcome to the Data Masking Tool</h1>
      <div style={{ marginTop: "20px" }}>
        <button
          onClick={() => navigate("/schema")}
          style={{
            padding: "10px 20px",
            marginRight: "10px",
            fontSize: "16px",
            cursor: "pointer",
          }}
        >
          Generate Config File
        </button>
        <button
          onClick={() => navigate("/editConfig")}
          style={{
            padding: "10px 20px",
            marginRight: "10px",
            fontSize: "16px",
            cursor: "pointer",
          }}
        >
          Update Config File
        </button>
        <button
          onClick={() => navigate("/performMask")}
          style={{
            padding: "10px 20px",
            fontSize: "16px",
            cursor: "pointer",
          }}
        >
          Perform Masking
        </button>
        
      </div>
    </div>
  );
}

export default HomePage;
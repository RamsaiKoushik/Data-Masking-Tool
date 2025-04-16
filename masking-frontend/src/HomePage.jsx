import React from "react";
import { useNavigate } from "react-router-dom";

function HomePage() {
  const navigate = useNavigate();

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h1>Welcome to the Data Masking Tool</h1>
      <div style={{ marginTop: "20px" }}>
        <button
          onClick={() => navigate("/editConfig")}
          style={{
            padding: "10px 20px",
            marginRight: "10px",
            fontSize: "16px",
            cursor: "pointer",
          }}
        >
          I have the config file
        </button>
        <button
          onClick={() => navigate("/schema")}
          style={{
            padding: "10px 20px",
            fontSize: "16px",
            cursor: "pointer",
          }}
        >
          I want to generate the config file
        </button>
      </div>
    </div>
  );
}

export default HomePage;
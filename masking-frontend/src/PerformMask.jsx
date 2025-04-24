import React, { useState } from "react";

export default function PerformMask() {
  const [schemaFile, setSchemaFile] = useState(null);
  const [selectedOption, setSelectedOption] = useState("Dump");
  const [isDumpReady, setIsDumpReady] = useState(false);

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      setSchemaFile(file);
    }
  };

  const handleOptionChange = (event) => {
    setSelectedOption(event.target.value);
    if (event.target.value === "Dump") {
      setIsDumpReady(false); // Reset dump readiness when switching options
    }
  };

  const handleSubmit = () => {
    if (!schemaFile) {
      alert("Please upload a schema file.");
      return;
    }
    if (!selectedOption) {
      alert("Please select an option.");
      return;
    }

    if (selectedOption === "Dump") {
      // Simulate dump file generation
      setTimeout(() => {
        alert("SQL Dump file is ready for download.");
        setIsDumpReady(true); // Mark the dump as ready
      }, 2000); // Simulate processing delay
    } else {
      alert("Data will be written to the database directly.");
      // Add logic for writing to the database
    }
  };

  const handleDownloadDump = () => {
    // Simulate dump file download
    const dumpContent = "This is a simulated SQL dump file.";
    const blob = new Blob([dumpContent], { type: "text/plain" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "dump.sql";
    link.click();
  };

  return (
    <div style={{ margin: "20px" }}>
      <h1>Perform Masking</h1>
      <div style={{ marginBottom: "20px" }}>
        <label>
          Upload Schema File:
          <input
            type="file"
            accept=".xml"
            onChange={handleFileUpload}
            style={{ marginLeft: "10px" }}
          />
        </label>
      </div>
      <div style={{ marginBottom: "20px" }}>
        <h3>Select an Option:</h3>
        <label>
          <input
            type="radio"
            value="Dump"
            checked={selectedOption === "Dump"}
            onChange={handleOptionChange}
            style={{ marginRight: "10px" }}
          />
          Generate SQL Dump File
        </label>
        <br />
        <label>
          <input
            type="radio"
            value="Direct"
            checked={selectedOption === "Direct"}
            onChange={handleOptionChange}
            style={{ marginRight: "10px" }}
          />
          Write to DB directly
        </label>
      </div>
      
      
      {selectedOption === "Dump" && (
        <button
          onClick={handleDownloadDump}
          style={{
            padding: "5px 5px",
            fontSize: "12px",
            cursor: "pointer",
          }}
        >
          Generate SQL Dump File
        </button>
      )}

      {selectedOption === "Direct" && (
        <div></div>
      )}
    </div>
  );
}
import React, { useState } from "react";
import {generateDump} from "../api/generateDump";
import { directWriteToDB } from "../api/directWriteToDB";
import { generateDumpFromKey } from "../api/generateDumpFromKey";

export default function PerformMask() {
  
  const [selectedOption, setSelectedOption] = useState("Dump");

  const [newDbUrl, setNewDbUrl] = useState("");
  const [dbName, setDbName] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  
  const [xmlContent, setXmlContent] = useState("");
  
  const [backendUrl, setBackendUrl] = useState("");

  const [dumpKey, setDumpKey] = useState("")

  const downloadFile = (data) => {
    const blob = new Blob([data], { type: 'text/sql' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'dump.sql'; 
    link.click();
  }

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const content = e.target.result; 
        setXmlContent(content); // Store the XML content in state
      };
      reader.readAsText(file); 
    }
  };

  const handleOptionChange = (event) => {
    setSelectedOption(event.target.value);
  };

  
  const handleDownloadDump = async () => {
    if (!xmlContent) {
      alert("Please upload a schema file first.");
      return;
    }
    setBackendUrl(generateDump(xmlContent))
  };

  
  const handleDownloadDumpFromKey = async () => {
    if(dumpKey == ""){
      alert("Please enter dump key.");
    } 
    downloadFile(await generateDumpFromKey(dumpKey))
  }

  
  const handleDirectWrite = () => {
    if(!xmlContent) {
      alert("Please upload a schema file first.");
      return;
    }
    if (!newDbUrl || !dbName || !username || !password) { 
      alert("Please fill in all database connection fields.");
      return;
    }
    if(directWriteToDB(newDbUrl, dbName, username, password, xmlContent) == false){
      alert("Did not work.")
    }
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
            value="DumpFromKey"
            checked={selectedOption === "DumpFromKey"}
            onChange={handleOptionChange}
            style={{ marginRight: "10px" }}
          />
          Generate SQL Dump File from existing Key
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
            <button onClick={handleDownloadDump}
                    style={{
                      padding: "5px 5px",
                      fontSize: "12px",
                      cursor: "pointer",
                    }}
                  >
                    Generate SQL Dump File Key
                  </button>
                  
                )}
      {selectedOption === "Dump" && backendUrl != "" && <div style={{"marginTop": "10px"}}>Your dump key is <b>{backendUrl}</b></div>}
  
       {selectedOption === "DumpFromKey" && 
        <div>
          
          <input
                type="text"
                placeholder="Dump File Key"
                value={dumpKey}
                onChange={(e) => setDumpKey(e.target.value)}
                style={{ width: "30%", marginBottom: "10px", padding: "8px" }}
              />

            <button
                onClick={handleDownloadDumpFromKey}
                style={{
                  padding: "10px",
                  fontSize: "14px",
                  cursor: "pointer",
                }}
              >   
              Get Dump File
              </button>

        </div>}         
      
      {selectedOption === "Direct" && (
            <div
              style={{
                maxWidth: "600px",
                padding: "20px",
                display: "flex",
                flexDirection: "column",
                alignItems: "center", // Center horizontally
                justifyContent: "center", // Center vertically
              }}
            >
              <input
                type="text"
                placeholder="New Database URL"
                value={newDbUrl}
                onChange={(e) => setNewDbUrl(e.target.value)}
                style={{ width: "100%", marginBottom: "10px", padding: "8px" }}
              />
              <input
                type="text"
                placeholder="New Database Name"
                value={dbName}
                onChange={(e) => setDbName(e.target.value)}
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
              <button
                onClick={() => {handleDirectWrite(newDbUrl, dbName, username, password)}}
                style={{
                  padding: "10px",
                  fontSize: "14px",
                  cursor: "pointer",
                }}
              >
                Write to DB
              </button>
            </div>
          )}
    </div>
  );
}
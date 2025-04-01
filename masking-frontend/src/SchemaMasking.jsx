import React, { useState } from "react";
import { useLocation } from "react-router-dom";

const MaskingStrategies = [
  { value: "none", label: "No Masking" },
  { value: "full", label: "Full Masking" },
  { value: "partial", label: "Partial Masking" },
  { value: "hash", label: "Hashing" },
  { value: "custom", label: "Custom Strategy" }
];

function SchemaMasking() {

  const { state } = useLocation();
  const schema = state?.schema;
  
  const [maskingConfig, setMaskingConfig] = useState({});

  const handleStrategyChange = (tableName, columnName, strategy) => {
    setMaskingConfig((prev) => ({
      ...prev,
      [tableName]: {
        ...prev[tableName],
        [columnName]: strategy
      }
    }));
  };

  const handleSubmit = () => {
    console.log("Masking configuration:", maskingConfig);
    alert("Masking configuration submitted. Check the console for details.");
  };

  return (
    <div style={{ marginTop: "20px" }}>
      <h3>Select Masking Strategies for Each Column</h3>
      {schema.map((table) => (
        <div key={table.table_name} style={{ marginBottom: "20px", border: "1px solid #ddd", padding: "10px" }}>
          <h4>Table: {table.table_name}</h4>
          {table.columns.map((col) => (
            <div key={col.column_name} style={{ marginLeft: "20px", marginBottom: "10px" }}>
              <label>
                Column: <strong>{col.column_name}</strong> – Masking Strategy:
                <select
                  onChange={(e) =>
                    handleStrategyChange(table.table_name, col.column_name, e.target.value)
                  }
                  defaultValue="none"
                  style={{ marginLeft: "10px" }}
                >
                  {MaskingStrategies.map((strategy) => (
                    <option key={strategy.value} value={strategy.value}>
                      {strategy.label}
                    </option>
                  ))}
                </select>
              </label>
            </div>
          ))}
        </div>
      ))}
      <button onClick={handleSubmit} style={{ padding: "10px", width: "100%" }}>
        Submit Masking Configurations
      </button>
    </div>
  );
}

export default SchemaMasking;

import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import generateXML from "../api/generateXML";

const MaskingStrategies = [
  { value: "no_masking", label: "No Masking" },
  { value: "shuffling", label: "Shuffling" },
  { value: "redaction", label: "Full Redaction" },
  { value: "partial_redaction", label: "Partial Redaction" },
  { value: "encryption", label: "Encryption" },
  { value: "lookup_sub", label: "Lookup Substitution" },
];

function SchemaMasking() {
  const { state } = useLocation();
  const schema = state?.schema;

  const [maskingConfig, setMaskingConfig] = useState(
    schema
      ? schema.reduce((acc, table) => {
          acc[table.table_name] = table.columns.reduce((colAcc, col) => {
            colAcc[col.column_name] = "no_masking"; // Default masking strategy
            return colAcc;
          }, {});
          return acc;
        }, {})
      : {}
  );

  const handleStrategyChange = (tableName, columnName, strategy) => {
    setMaskingConfig((prev) => ({
      ...prev,
      [tableName]: {
        ...prev[tableName],
        [columnName]: strategy,
      },
    }));
  };

  const isPrimaryKey = (table, columnName) =>
    table.primary_keys.includes(columnName);

  const isForeignKey = (table, columnName) =>
    table.foreign_keys.some((fk) => fk.column_name === columnName);

  const handleSubmit = () => {
    generateXML(schema, maskingConfig, state?.dbUrl, state?.username, state?.password);
  };

  if (!schema) {
    return <p>No schema data available. Please fetch the schema first.</p>;
  }

  return (
    <div style={{ marginTop: "20px" }}>
      <h3>Select Masking Strategies for Each Column</h3>
      {schema.map((table) => (
        <div
          key={table.table_name}
          style={{
            marginBottom: "20px",
            border: "1px solid #ddd",
            padding: "10px",
          }}
        >
          <h4>Table: {table.table_name}</h4>
          {table.columns.map((col) => (
            <div
              key={col.column_name}
              style={{ marginLeft: "20px", marginBottom: "10px" }}
            >
              <label>
                Column: <strong>{col.column_name}</strong>
                {isPrimaryKey(table, col.column_name) && (
                  <span style={{ color: "green", marginLeft: "5px" }}>
                    (Primary Key)
                  </span>
                )}
                {isForeignKey(table, col.column_name) && (
                  <span style={{ color: "blue", marginLeft: "5px" }}>
                    (Foreign Key)
                  </span>
                )}
                :
                <select
                  onChange={(e) =>
                    handleStrategyChange(
                      table.table_name,
                      col.column_name,
                      e.target.value
                    )
                  }
                  value={maskingConfig[table.table_name][col.column_name]} // Default value
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
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          marginTop: "20px",
        }}
      >
        <button onClick={handleSubmit} style={{ padding: "10px" }}>
          Submit Configuration
        </button>
      </div>
    </div>
  );
}

export default SchemaMasking;
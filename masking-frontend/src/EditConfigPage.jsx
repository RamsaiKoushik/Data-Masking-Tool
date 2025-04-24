import React, { useState } from "react";
import generateXML from "../api/generateXML";
import { MaskingStrategies } from "../api/maskingStrategies";

function EditConfigPage() {
  
  const isPrimaryKey = (table, columnName) =>
    table.primary_keys.includes(columnName);

  const isForeignKey = (table, columnName) =>
    table.foreign_keys.some((fk) => fk.column_name === columnName);

  const [configData, setConfigData] = useState(null);
  const [isFileUploaded, setIsFileUploaded] = useState(false);

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const xmlContent = e.target.result;
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlContent, "application/xml");
        const database = xmlDoc.querySelector("database");
        const db_name = database.querySelector("db_url").textContent + database.querySelector("db_name").textContent
        console.log(db_name);
        const username = database.querySelector("username").textContent;
        const password = database.querySelector("password").textContent;

        const tables = Array.from(database.querySelectorAll("table")).map(
          (table) => ({
            
            table_name: table.querySelector("table_name").textContent,
            to_mask: table.querySelector("to_mask").textContent,
            primary_keys: Array.from( table.querySelectorAll("primary_key")).map((key) => key.textContent),
            unique_columns: Array.from(table.querySelectorAll("unique_column")).map((col) => col.textContent),
            
            columns: Array.from(table.querySelectorAll("column")).map((col) => ({
              column_name: col.querySelector("column_name").textContent,
              data_type: col.querySelector("data_type")?.textContent || "",
              nullable: col.querySelector("nullable")?.textContent || "",
              auto_increment: col.querySelector("auto_increment")?.textContent || "",
              column_size: col.querySelector("column_size")?.textContent || "",
              default_value: col.querySelector("default_value")?.textContent || "",
              masking_strategy: col.querySelector("masking_strategy").textContent,
            })),
            foreign_keys: Array.from(table.querySelectorAll("foreign_key")).map((fk) => ({
              foreign_table: fk.querySelector("foreign_table")?.textContent || "",
              column_name: fk.querySelector("column_name")?.textContent || "",
              foreign_column: fk.querySelector("foreign_column")?.textContent || "",
            })),
          })
        );

        setConfigData({ db_name, username, password, tables });
        setIsFileUploaded(true);
      };
      reader.readAsText(file);
    }
  };

  const handleTableMaskChange = (tableName, value) => {
    setConfigData((prev) => ({
      ...prev,
      tables: prev.tables.map((table) =>
        table.table_name === tableName ? { ...table, to_mask: value } : table
      ),
    }));
  };

  const handleMaskingStrategyChange = (tableName, columnName, strategy) => {
    setConfigData((prev) => ({
      ...prev,
      tables: prev.tables.map((table) =>
        table.table_name === tableName
          ? {
              ...table,
              columns: table.columns.map((col) =>
                col.column_name === columnName
                  ? { ...col, masking_strategy: strategy }
                  : col
              ),
            }
          : table
      ),
    }));
  };

  const handleDownloadXML = () => {
    if (!configData) {
      alert("No configuration data available to generate XML.");
      return;
    }

    // Prepare the schema and maskingConfig for generateXML
    const schema = configData.tables.map((table) => ({
      table_name: table.table_name,
      to_mask: table.to_mask,
      primary_keys: table.primary_keys || [],
      unique_columns: table.unique_columns || [],
      columns: table.columns.map((col) => ({
        column_name: col.column_name,
        data_type: col.data_type,
        nullable: col.nullable,
        auto_increment: col.auto_increment,
        column_size: col.column_size,
        default_value: col.default_value,
        masking_strategy: col.masking_strategy,
      })),
      foreign_keys: table.foreign_keys || [],
    }));

    const maskingConfig = schema.reduce((acc, table) => {
      acc[table.table_name] = table.columns.reduce((colAcc, col) => {
        colAcc[col.column_name] = col.masking_strategy;
        return colAcc;
      }, {});
      return acc;
    }, {});

    generateXML(schema, maskingConfig, configData.db_name, configData.username, configData.password);
  };

  if (!configData) {
    return (
      <div style={{ textAlign: "center", marginTop: "50px" }}>
        <h1>Upload XML Config File</h1>
        <input
          type="file"
          accept=".xml"
          onChange={handleFileUpload}
          style={{ marginTop: "20px" }}
        />
        {isFileUploaded && (
          <button
            onClick={() => alert("File uploaded successfully!")}
            style={{
              padding: "10px 20px",
              fontSize: "16px",
              cursor: "pointer",
              marginTop: "20px",
            }}
          >
            Submit
          </button>
        )}
      </div>
    );
  }

  return (
    <div style={{ margin: "20px" }}>
      <h1>Edit Configuration</h1>
      {configData.tables.map((table) => (
        <div
          key={table.table_name}
          style={{
            border: "1px solid #ddd",
            padding: "10px",
            marginBottom: "20px",
          }}
        >
          <h3>
            Table: {table.table_name}{" "}
            <select
              value={table.to_mask}
              onChange={(e) =>
                handleTableMaskChange(table.table_name, e.target.value)
              }
              style={{ marginLeft: "10px" }}
            >
              <option value="Yes">Yes</option>
              <option value="No">No</option>
            </select>
          </h3>
          <h4>Columns:</h4>
          {table.columns.map((col) => (
            <div key={col.column_name} style={{ marginBottom: "10px", marginLeft: "20px" }}>
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
                - Masking Strategy:
                <select
                  value={col.masking_strategy}
                  onChange={(e) =>
                    handleMaskingStrategyChange(
                      table.table_name,
                      col.column_name,
                      e.target.value
                    )
                  }
                  style={{ marginLeft: "10px" }}
                >
                  {isForeignKey(table, col.column_name)
                    ? // Only show LookupSubstitution for foreign keys
                      MaskingStrategies.filter(
                        (strategy) => strategy.value === "LookupSubstitution"
                      ).map((strategy) => (
                        <option key={strategy.value} value={strategy.value}>
                          {strategy.label}
                        </option>
                      ))
                    : // Show all strategies for non-foreign keys
                      MaskingStrategies.map((strategy) => (
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
      <button
        onClick={handleDownloadXML}
        style={{
          padding: "10px 20px",
          fontSize: "16px",
          cursor: "pointer",
          marginTop: "20px",
        }}
      >
        Download XML Config
      </button>
    </div>
  );
}

export default EditConfigPage;
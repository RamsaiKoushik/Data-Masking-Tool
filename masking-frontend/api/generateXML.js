const generateXML = (schema, maskingConfig, user_dbUrl, user_username, user_password) => {
    if (!schema) return;
  
    const xmlDoc = document.implementation.createDocument("", "", null);
    const database = xmlDoc.createElement("database");
    database.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    database.setAttribute("xsi:noNamespaceSchemaLocation", "config.xsd");
  
    const dbName = xmlDoc.createElement("db_name");
    dbName.textContent = user_dbUrl; // Replace with actual database name
    database.appendChild(dbName);
  
    const username = xmlDoc.createElement("username");
    username.textContent = user_username; // Replace with actual username
    database.appendChild(username);
  
    const password = xmlDoc.createElement("password");
    password.textContent = user_password; // Replace with actual password
    database.appendChild(password);
  
    const tables = xmlDoc.createElement("tables");
  
    schema.forEach((table) => {
      const tableElement = xmlDoc.createElement("table");
  
      const tableName = xmlDoc.createElement("table_name");
      tableName.textContent = table.table_name;
      tableElement.appendChild(tableName);
  
      const primaryKeys = xmlDoc.createElement("primary_keys");
      table.primary_keys.forEach((key) => {
        const primaryKey = xmlDoc.createElement("primary_key");
        primaryKey.textContent = key;
        primaryKeys.appendChild(primaryKey);
      });
      tableElement.appendChild(primaryKeys);
  
      const uniqueColumns = xmlDoc.createElement("unique_columns");
      table.unique_columns.forEach((column) => {
        const uniqueColumn = xmlDoc.createElement("unique_column");
        uniqueColumn.textContent = column;
        uniqueColumns.appendChild(uniqueColumn);
      });
      tableElement.appendChild(uniqueColumns);
  
      const columns = xmlDoc.createElement("columns");
      table.columns.forEach((col) => {
        const column = xmlDoc.createElement("column");
  
        const columnName = xmlDoc.createElement("column_name");
        columnName.textContent = col.column_name;
        column.appendChild(columnName);
  
        const dataType = xmlDoc.createElement("data_type");
        dataType.textContent = col.data_type;
        column.appendChild(dataType);
  
        const nullable = xmlDoc.createElement("nullable");
        nullable.textContent = col.nullable;
        column.appendChild(nullable);
  
        const autoIncrement = xmlDoc.createElement("auto_increment");
        autoIncrement.textContent = col.auto_increment;
        column.appendChild(autoIncrement);
  
        const columnSize = xmlDoc.createElement("column_size");
        columnSize.textContent = col.column_size || ""; // Add column size
        column.appendChild(columnSize);
  
        const defaultValue = xmlDoc.createElement("default_value");
        defaultValue.textContent = col.default_value || "";
        column.appendChild(defaultValue);
  
        const maskingStrategy = xmlDoc.createElement("masking_strategy");
        maskingStrategy.textContent = maskingConfig[table.table_name]?.[col.column_name] || "none";
        column.appendChild(maskingStrategy);
  
        columns.appendChild(column);
      });
      tableElement.appendChild(columns);
  
      const foreignKeys = xmlDoc.createElement("foreign_keys");
      table.foreign_keys.forEach((fk) => {
        const foreignKey = xmlDoc.createElement("foreign_key");
  
        const foreignTable = xmlDoc.createElement("foreign_table");
        foreignTable.textContent = fk.foreign_table;
        foreignKey.appendChild(foreignTable);
  
        const columnName = xmlDoc.createElement("column_name");
        columnName.textContent = fk.column_name;
        foreignKey.appendChild(columnName);
  
        const foreignColumn = xmlDoc.createElement("foreign_column");
        foreignColumn.textContent = fk.foreign_column;
        foreignKey.appendChild(foreignColumn);
  
        foreignKeys.appendChild(foreignKey);
      });
      tableElement.appendChild(foreignKeys);
  
      tables.appendChild(tableElement);
    });
  
    database.appendChild(tables);
    xmlDoc.appendChild(database);
  
    const serializer = new XMLSerializer();
    const xmlString = serializer.serializeToString(xmlDoc);
  
    const blob = new Blob([xmlString], { type: "application/xml" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "config.xml";
    link.click();
};
  
  export default generateXML;
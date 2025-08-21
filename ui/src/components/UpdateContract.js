import React, { useEffect, useState } from "react";
import API_CONFIG from "../config";
import "./../styles.css";

export default function UpdateContract() {
  const [ids, setIds] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [contract, setContract] = useState(null);
  const [result, setResult] = useState("");

  useEffect(() => {
    fetch(API_CONFIG.BASE_URL + "/api/contracts/all-ids")
      .then(res => res.json())
      .then(setIds);
  }, []);

  const handleLoad = () => {
    if (!selectedId) return;
    fetch(API_CONFIG.BASE_URL + "/api/contracts/get/" + selectedId)
      .then(res => res.json())
      .then(data => setContract(data));
  };

  const addTable = () => {
    setContract({
      ...contract,
      physical_model: {
        physical_tables: [
          ...contract.physical_model.physical_tables,
          {
            table_name: "",
            table_description: "",
            physical_fields: [],
            partitioning_fields: "",
            clustering_fields: ""
          }
        ]
      }
    });
  };

  const removeTable = (tIdx) => {
    const tables = [...contract.physical_model.physical_tables];
    tables.splice(tIdx, 1);
    setContract({
      ...contract,
      physical_model: { physical_tables: tables }
    });
  };

  const addField = (tIdx) => {
    const tables = [...contract.physical_model.physical_tables];
    tables[tIdx].physical_fields = tables[tIdx].physical_fields || [];
    tables[tIdx].physical_fields.push({
      nested_fields: [],
      field_name: "",
      field_type: "",
      field_description: ""
    });
    setContract({
      ...contract,
      physical_model: { physical_tables: tables }
    });
  };

  const removeField = (tIdx, fIdx) => {
    const tables = [...contract.physical_model.physical_tables];
    tables[tIdx].physical_fields.splice(fIdx, 1);
    setContract({
      ...contract,
      physical_model: { physical_tables: tables }
    });
  };

  const addNestedField = (tIdx, fIdx) => {
    const tables = [...contract.physical_model.physical_tables];
    tables[tIdx].physical_fields[fIdx].nested_fields = tables[tIdx].physical_fields[fIdx].nested_fields || [];
    tables[tIdx].physical_fields[fIdx].nested_fields.push({
      field_name: "",
      field_type: "",
      field_description: "",
      nested_fields: []
    });
    setContract({
      ...contract,
      physical_model: { physical_tables: tables }
    });
  };

  const removeNestedField = (tIdx, fIdx, nIdx) => {
    const tables = [...contract.physical_model.physical_tables];
    tables[tIdx].physical_fields[fIdx].nested_fields.splice(nIdx, 1);
    setContract({
      ...contract,
      physical_model: { physical_tables: tables }
    });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name.startsWith("big_query_dataset.")) {
      setContract({
        ...contract,
        big_query_dataset: {
          ...contract.big_query_dataset,
          [name.split(".")[1]]: value
        }
      });
    } else if (name.startsWith("physical_tables.")) {
      const [, field, tIdx] = name.split(".");
      const tables = [...contract.physical_model.physical_tables];
      tables[tIdx][field] = value;
      setContract({
        ...contract,
        physical_model: { physical_tables: tables }
      });
    } else if (name.startsWith("physical_fields.")) {
      const [, field, tIdx, fIdx] = name.split(".");
      const tables = [...contract.physical_model.physical_tables];
      tables[tIdx].physical_fields[fIdx][field] = value;
      setContract({
        ...contract,
        physical_model: { physical_tables: tables }
      });
    } else if (name.startsWith("nested_fields.")) {
      const [, field, tIdx, fIdx, nIdx] = name.split(".");
      const tables = [...contract.physical_model.physical_tables];
      tables[tIdx].physical_fields[fIdx].nested_fields[nIdx][field] = value;
      setContract({
        ...contract,
        physical_model: { physical_tables: tables }
      });
    } else {
      setContract({ ...contract, [name]: value });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(API_CONFIG.BASE_URL + "/api/contracts/update", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(contract)
      });
      const text = await res.text();
      try {
        setResult(JSON.stringify(JSON.parse(text), null, 2));
      } catch {
        setResult(text);
      }
    } catch (err) {
      setResult("Error: " + err);
    }
  };

  return (
    <div className="container">
      <h1>Update Data Contract</h1>
      <div>
        <label>Select Contract ID:</label>
        <select value={selectedId} onChange={e => setSelectedId(e.target.value)}>
          <option value="">--Select--</option>
          {ids.map(id => (
            <option key={id} value={id}>{id}</option>
          ))}
        </select>
        <button onClick={handleLoad}>Load Contract</button>
      </div>
      {contract && (
        <form onSubmit={handleSubmit}>
          <div className="form-section">
            <h3>General Info</h3>
            <input type="text" name="data_product_name" placeholder="Data Product Name" value={contract.data_product_name} onChange={handleChange} required />
            <input type="text" name="version" placeholder="Version" value={contract.version} onChange={handleChange} required />
            <input type="text" name="data_owner" placeholder="Data Owner" value={contract.data_owner} onChange={handleChange} required />
            <input type="text" name="description" placeholder="Description" value={contract.description} onChange={handleChange} required />
          </div>
          <div className="form-section">
            <h3>BigQuery Dataset</h3>
            <input type="text" name="big_query_dataset.project_id" placeholder="Project ID" value={contract.big_query_dataset.project_id} onChange={handleChange} required />
            <input type="text" name="big_query_dataset.data_set_name" placeholder="Dataset Name" value={contract.big_query_dataset.data_set_name} onChange={handleChange} required />
          </div>
          <div className="form-section">
            <h3>Physical Tables</h3>
            <button type="button" onClick={addTable}>Add Table</button>
            {contract.physical_model.physical_tables.map((table, tIdx) => (
              <div key={tIdx} style={{border: "1px solid #b3e5fc", padding: "10px", marginBottom: "10px", borderRadius: "6px"}}>
                <input type="text" name={`physical_tables.table_name.${tIdx}`} placeholder="Table Name" value={table.table_name} onChange={handleChange} required />
                <input type="text" name={`physical_tables.table_description.${tIdx}`} placeholder="Table Description" value={table.table_description} onChange={handleChange} required />
                <input type="text" name={`physical_tables.partitioning_fields.${tIdx}`} placeholder="Partitioning Fields" value={table.partitioning_fields} onChange={handleChange} />
                <input type="text" name={`physical_tables.clustering_fields.${tIdx}`} placeholder="Clustering Fields" value={table.clustering_fields} onChange={handleChange} />
                <button type="button" onClick={() => removeTable(tIdx)} style={{marginBottom: "10px"}}>Remove Table</button>
                <h4>Physical Fields</h4>
                <button type="button" onClick={() => addField(tIdx)}>Add Field</button>
                {table.physical_fields && table.physical_fields.map((field, fIdx) => (
                  <div key={fIdx} style={{marginLeft: "10px", borderBottom: "1px solid #e0f7fa", paddingBottom: "8px", marginBottom: "8px"}}>
                    <input type="text" name={`physical_fields.field_name.${tIdx}.${fIdx}`} placeholder="Field Name" value={field.field_name} onChange={handleChange} required />
                    <input type="text" name={`physical_fields.field_type.${tIdx}.${fIdx}`} placeholder="Field Type" value={field.field_type} onChange={handleChange} required />
                    <input type="text" name={`physical_fields.field_description.${tIdx}.${fIdx}`} placeholder="Field Description" value={field.field_description} onChange={handleChange} />
                    <button type="button" onClick={() => addNestedField(tIdx, fIdx)} style={{marginLeft: "10px"}}>Add Nested Field</button>
                    {field.nested_fields && field.nested_fields.map((nested, nIdx) => (
                      <div key={nIdx} style={{marginLeft: "20px", borderLeft: "2px solid #b3e5fc", paddingLeft: "10px", marginBottom: "8px"}}>
                        <input type="text" name={`nested_fields.field_name.${tIdx}.${fIdx}.${nIdx}`} placeholder="Nested Field Name" value={nested.field_name} onChange={handleChange} required />
                        <input type="text" name={`nested_fields.field_type.${tIdx}.${fIdx}.${nIdx}`} placeholder="Nested Field Type" value={nested.field_type} onChange={handleChange} required />
                        <input type="text" name={`nested_fields.field_description.${tIdx}.${fIdx}.${nIdx}`} placeholder="Nested Field Description" value={nested.field_description} onChange={handleChange} />
                        <button type="button" onClick={() => removeNestedField(tIdx, fIdx, nIdx)} style={{marginLeft: "10px"}}>Remove Nested Field</button>
                      </div>
                    ))}
                    <button type="button" onClick={() => removeField(tIdx, fIdx)} style={{marginLeft: "10px"}}>Remove Field</button>
                  </div>
                ))}
              </div>
            ))}
          </div>
          <button type="submit">Save Changes</button>
        </form>
      )}
      {result && (
        <div className={result.startsWith("Error") ? "alert-error" : "alert-success"}>
          {result}
        </div>
      )}
    </div>
  );
}
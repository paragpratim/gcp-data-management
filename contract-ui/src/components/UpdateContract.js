import React, { useEffect, useState } from "react";
import API_CONFIG from "../config";
import "./../styles.css";

export default function UpdateContract() {
  const [ids, setIds] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [contract, setContract] = useState(null);
  const [result, setResult] = useState("");
  const [projects, setProjects] = useState([]);
  const [datasets, setDatasets] = useState([]);

  useEffect(() => {
    fetch(API_CONFIG.BASE_URL + "/api/contracts/all-ids")
      .then(res => res.json())
      .then(setIds);
  }, []);

  // Fetch projects on component mount
  useEffect(() => {
    fetch(API_CONFIG.BASE_URL + "/api/gcp/getProjects")
      .then(res => res.json())
      .then(data => setProjects(data))
      .catch(err => console.error("Failed to fetch projects:", err));
  }, []);

  // Fetch datasets when project ID changes
  useEffect(() => {
    if (contract && contract.big_query_dataset && contract.big_query_dataset.project_id) {
      fetch(API_CONFIG.BASE_URL + `/api/gcp/getBigQueryDatasets/${contract.big_query_dataset.project_id}`)
        .then(res => res.json())
        .then(data => setDatasets(data))
        .catch(err => {
          console.error("Failed to fetch datasets:", err);
          setDatasets([]);
        });
    } else {
      setDatasets([]);
    }
  }, [contract]); 

  const handleLoad = () => {
    if (!selectedId) return;
    
    setResult("Loading contract...");
    
    fetch(API_CONFIG.BASE_URL + "/api/contracts/get/" + selectedId)
      .then(res => {
        if (!res.ok) {
          throw new Error(`HTTP ${res.status}: ${res.statusText}`);
        }
        return res.json();
      })
      .then(data => {
        console.log("Raw contract data:", data);
        
        // ✅ Ensure we have the right structure
        if (!data.physical_model || !data.physical_model.physical_tables) {
          console.error("Invalid contract structure:", data);
          setResult("Error: Invalid contract structure");
          return;
        }
        
        // ✅ Mark existing tables and fields immediately when loading
        const markedData = {
          ...data,
          physical_model: {
            ...data.physical_model,
            physical_tables: data.physical_model.physical_tables.map((table, index) => {
              console.log(`Marking table ${index} as existing:`, table.table_name);
              return {
                ...table,
                _existing: true, // Mark as existing
                physical_fields: (table.physical_fields || []).map((field, fieldIndex) => {
                  console.log(`Marking field ${fieldIndex} as existing:`, field.field_name);
                  return {
                    ...field,
                    _existing: true // Mark as existing
                  };
                })
              };
            })
          },
          _fieldsMarked: true // Flag to prevent re-marking
        };
        
        console.log("Marked contract data:", markedData);
        setContract(markedData);
        setResult("Contract loaded successfully");
      })
      .catch(err => {
        console.error("Error loading contract:", err);
        setResult("Error loading contract: " + err.message);
      });
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
            clustering_fields: "",
            _existing: false // ✅ Mark new tables as NOT existing
          }
        ]
      }
    });
  };

  const removeTable = (tIdx) => {
    const tables = [...contract.physical_model.physical_tables];
    
    // ✅ Better check for existing tables
    console.log("Attempting to remove table:", tIdx, "Existing flag:", tables[tIdx]._existing);
    
    if (tables[tIdx]._existing === true) {
      console.log("Cannot remove existing table");
      setResult("Cannot delete existing tables. Only newly added tables can be removed.");
      return; // Don't allow deletion of existing tables
    }
    
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
      field_description: "",
      _existing: false // ✅ Mark new fields as NOT existing
    });
    setContract({
      ...contract,
      physical_model: { physical_tables: tables }
    });
  };

  const removeField = (tIdx, fIdx) => {
    const tables = [...contract.physical_model.physical_tables];
    
    // ✅ Check if field is existing
    if (tables[tIdx].physical_fields[fIdx]._existing === true) {
      setResult("Cannot delete existing fields. Only newly added fields can be removed.");
      return;
    }
    
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
    
    // ✅ Prevent version field changes
    if (name === "version") {
      return; // Don't update version field
    }
    
    if (name.startsWith("big_query_dataset.")) {
      const field = name.split(".")[1];
      setContract({
        ...contract,
        big_query_dataset: {
          ...contract.big_query_dataset,
          [field]: value,
          // Clear dataset name when project changes
          ...(field === "project_id" && { data_set_name: "" })
        }
      });
    } else if (name.startsWith("physical_tables.")) {
      const [, field, tIdx] = name.split(".");
      const tables = [...contract.physical_model.physical_tables];
      
      // ✅ Prevent editing table_name for existing tables
      if (field === "table_name" && tables[tIdx]._existing === true) {
        console.log("Cannot edit existing table name");
        return; // Don't allow editing existing table names
      }
      
      tables[tIdx][field] = value;
      setContract({
        ...contract,
        physical_model: { physical_tables: tables }
      });
    } else if (name.startsWith("physical_fields.")) {
      const [, field, tIdx, fIdx] = name.split(".");
      // Prevent editing existing physical fields
      if (contract.physical_model.physical_tables[tIdx].physical_fields[fIdx]._existing === true) {
        console.log("Cannot edit existing field");
        return;
      }
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
      // ✅ Clean the contract data before sending (remove internal flags)
      const cleanContract = {
        ...contract,
        physical_model: {
          physical_tables: contract.physical_model.physical_tables.map(table => {
            const { _existing, ...cleanTable } = table;
            return {
              ...cleanTable,
              physical_fields: (cleanTable.physical_fields || []).map(field => {
                const { _existing: fieldExisting, ...cleanField } = field;
                return cleanField;
              })
            };
          })
        }
      };
      delete cleanContract._fieldsMarked;

      const res = await fetch(API_CONFIG.BASE_URL + `/api/contracts/update`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cleanContract)
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
            {/* ✅ Make version field uneditable */}
            <input 
              type="text" 
              name="version" 
              placeholder="Version (Auto-generated on update)" 
              value={`${contract.version} → New version will be auto-generated`}
              onChange={handleChange} 
              disabled={true}
              style={{ 
                backgroundColor: '#f0f8ff', 
                cursor: 'not-allowed',
                color: '#0066cc',
                border: '2px solid #b3d9ff',
                fontStyle: 'italic'
              }}
              title="A new version will be automatically generated when you update this contract"
            />
            <input type="text" name="data_owner" placeholder="Data Owner" value={contract.data_owner} onChange={handleChange} required />
            <textarea
              name="description"
              placeholder="Description"
              value={contract.description}
              onChange={handleChange}
              required
              rows={3}
            />
          </div>
          <div className="form-section">
            <h3>BigQuery Dataset</h3>
            <select 
              name="big_query_dataset.project_id" 
              value={contract.big_query_dataset.project_id} 
              onChange={handleChange} 
              required
            >
              <option value="">Select Project ID</option>
              {projects.map((project) => (
                <option key={project.gcp_project_record_id} value={project.gcp_project_id}>
                  {project.gcp_project_id}
                </option>
              ))}
            </select>
            <select 
              name="big_query_dataset.data_set_name" 
              value={contract.big_query_dataset.data_set_name} 
              onChange={handleChange} 
              required
              disabled={!contract.big_query_dataset.project_id}
            >
              <option value="">Select Dataset Name</option>
              {datasets.map((dataset, index) => (
                <option key={index} value={dataset.data_set_name}>
                  {dataset.data_set_name}
                </option>
              ))}
            </select>
          </div>
          <div className="form-section">
            <h3>Physical Tables</h3>
            <button type="button" onClick={addTable}>Add Table</button>
            {contract.physical_model.physical_tables.map((table, tIdx) => (
              <div key={tIdx} style={{
                border: table._existing ? "2px solid #e0e0e0" : "1px solid #b3e5fc", 
                padding: "10px", 
                marginBottom: "10px", 
                borderRadius: "6px",
                backgroundColor: table._existing ? "#fafafa" : "white"
              }}>
                {/* ✅ Removed both indicator texts - clean interface */}
                
                <input 
                  type="text" 
                  name={`physical_tables.table_name.${tIdx}`} 
                  placeholder="Table Name" 
                  value={table.table_name} 
                  onChange={handleChange} 
                  required 
                  disabled={table._existing}
                  style={table._existing ? { background: "#f0f0f0", color: "#888" } : {}}
                />
                <input 
                  type="text" 
                  name={`physical_tables.table_description.${tIdx}`} 
                  placeholder="Table Description" 
                  value={table.table_description} 
                  onChange={handleChange} 
                  required 
                />
                <input 
                  type="text" 
                  name={`physical_tables.partitioning_fields.${tIdx}`} 
                  placeholder="Partitioning Fields" 
                  value={table.partitioning_fields || ""} 
                  onChange={handleChange} 
                />
                <input 
                  type="text" 
                  name={`physical_tables.clustering_fields.${tIdx}`} 
                  placeholder="Clustering Fields" 
                  value={table.clustering_fields || ""} 
                  onChange={handleChange} 
                />
                
                {/* ✅ Remove button section remains the same */}
                <div style={{ marginBottom: "10px" }}>
                  {table._existing ? (
                    <div
                      style={{
                        backgroundColor: "#e0e0e0",
                        color: "#aaa",
                        border: "none",
                        padding: "8px 16px",
                        borderRadius: "4px",
                        fontSize: "14px",
                        fontWeight: "500",
                        cursor: "not-allowed",
                        textAlign: "center",
                        userSelect: "none"
                      }}
                      title="Cannot delete existing tables"
                    >
                      🔒 Cannot Delete (Existing)
                    </div>
                  ) : (
                    <button 
                      type="button" 
                      onClick={() => {
                        console.log("Removing table at index:", tIdx);
                        removeTable(tIdx);
                      }}
                      style={{
                        backgroundColor: "#dc3545",
                        color: "white",
                        cursor: "pointer",
                        border: "none",
                        padding: "8px 16px",
                        borderRadius: "4px",
                        fontSize: "14px",
                        fontWeight: "500"
                      }}
                      title="Remove this table"
                    >
                      🗑️ Remove Table
                    </button>
                  )}
                </div>
                
                <h4>Physical Fields</h4>
                <button type="button" onClick={() => addField(tIdx)}>Add Field</button>
                <table className="physical-fields-table" style={{tableLayout: "fixed", width: "100%"}}>
                  <thead>
                    <tr>
                      <th style={{width: "18%"}}>Field Name</th>
                      <th style={{width: "18%"}}>Field Type</th>
                      <th style={{width: "18%"}}>Field Description</th>
                      <th style={{width: "32%"}}>Nested Fields</th>
                      <th style={{width: "14%"}}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {table.physical_fields && table.physical_fields.map((field, fIdx) => (
                      <tr key={fIdx}>
                        <td>
                          <input
                            type="text"
                            name={`physical_fields.field_name.${tIdx}.${fIdx}`}
                            placeholder="Field Name"
                            value={field.field_name}
                            onChange={handleChange}
                            required
                            disabled={field._existing}
                            style={field._existing ? { background: "#f0f0f0", color: "#888" } : {}}
                          />
                        </td>
                        <td>
                          <select
                            name={`physical_fields.field_type.${tIdx}.${fIdx}`}
                            value={field.field_type}
                            onChange={handleChange}
                            required
                            disabled={field._existing}
                            style={field._existing ? { background: "#f0f0f0", color: "#888" } : {}}
                          >
                            <option value="">Select Type</option>
                            <option value="STRING">STRING</option>
                            <option value="BYTES">BYTES</option>
                            <option value="INT64">INT64</option>
                            <option value="FLOAT64">FLOAT64</option>
                            <option value="NUMERIC">NUMERIC</option>
                            <option value="BIGNUMERIC">BIGNUMERIC</option>
                            <option value="BOOL">BOOL</option>
                            <option value="TIMESTAMP">TIMESTAMP</option>
                            <option value="DATE">DATE</option>
                            <option value="TIME">TIME</option>
                            <option value="DATETIME">DATETIME</option>
                            <option value="GEOGRAPHY">GEOGRAPHY</option>
                            <option value="ARRAY">ARRAY</option>
                            <option value="STRUCT">STRUCT</option>
                          </select>
                        </td>
                        <td>
                          <input
                            type="text"
                            name={`physical_fields.field_description.${tIdx}.${fIdx}`}
                            placeholder="Field Description"
                            value={field.field_description}
                            onChange={handleChange}
                            disabled={field._existing}
                            style={field._existing ? { background: "#f0f0f0", color: "#888" } : {}}
                          />
                        </td>
                        <td>
                          <button
                            type="button"
                            className="table-action-btn"
                            title="Add Nested Field"
                            onClick={() => addNestedField(tIdx, fIdx)}
                            disabled={field._existing}
                          >
                            +
                          </button>
                          {field.nested_fields && field.nested_fields.map((nested, nIdx) => (
                            <div key={nIdx} className="nested-field-row">
                              <input
                                type="text"
                                name={`nested_fields.field_name.${tIdx}.${fIdx}.${nIdx}`}
                                placeholder="Nested Field Name"
                                value={nested.field_name}
                                onChange={handleChange}
                                required
                                disabled={field._existing}
                                style={field._existing ? { background: "#f0f0f0", color: "#888" } : {}}
                              />
                              <select
                                name={`nested_fields.field_type.${tIdx}.${fIdx}.${nIdx}`}
                                value={nested.field_type}
                                onChange={handleChange}
                                required
                                disabled={field._existing}
                                style={field._existing ? { background: "#f0f0f0", color: "#888" } : {}}
                              >
                                <option value="">Select Type</option>
                                <option value="STRING">STRING</option>
                                <option value="BYTES">BYTES</option>
                                <option value="INT64">INT64</option>
                                <option value="FLOAT64">FLOAT64</option>
                                <option value="NUMERIC">NUMERIC</option>
                                <option value="BIGNUMERIC">BIGNUMERIC</option>
                                <option value="BOOL">BOOL</option>
                                <option value="TIMESTAMP">TIMESTAMP</option>
                                <option value="DATE">DATE</option>
                                <option value="TIME">TIME</option>
                                <option value="DATETIME">DATETIME</option>
                                <option value="GEOGRAPHY">GEOGRAPHY</option>
                                <option value="ARRAY">ARRAY</option>
                                <option value="STRUCT">STRUCT</option>
                              </select>
                              <input
                                type="text"
                                name={`nested_fields.field_description.${tIdx}.${fIdx}.${nIdx}`}
                                placeholder="Description"
                                value={nested.field_description}
                                onChange={handleChange}
                                disabled={field._existing}
                                style={field._existing ? { background: "#f0f0f0", color: "#888" } : {}}
                              />
                              <button
                                type="button"
                                className="table-action-btn"
                                title="Remove Nested Field"
                                onClick={() => removeNestedField(tIdx, fIdx, nIdx)}
                                disabled={field._existing}
                                style={field._existing ? { background: "#e0e0e0", color: "#aaa", cursor: "not-allowed" } : {}}
                              >
                                −
                              </button>
                            </div>
                          ))}
                        </td>
                        <td>
                          <button
                            type="button"
                            className="table-action-btn"
                            title="Remove Field"
                            onClick={() => removeField(tIdx, fIdx)}
                            disabled={field._existing}
                          >
                            −
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
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
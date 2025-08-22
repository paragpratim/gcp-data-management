import React, { useEffect, useState } from "react";
import API_CONFIG from "../config";
import "./../styles.css";

export default function ViewContract() {
  const [ids, setIds] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [contract, setContract] = useState(null);

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

  return (
    <div className="container">
      <h1>View Data Contract</h1>
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
        <div>
          <div className="form-section">
            <h3>General Info</h3>
            <div><strong>Data Product Name:</strong> {contract.data_product_name}</div>
            <div><strong>Version:</strong> {contract.version}</div>
            <div><strong>Data Owner:</strong> {contract.data_owner}</div>
            <div><strong>Description:</strong> {contract.description}</div>
          </div>
          <div className="form-section">
            <h3>BigQuery Dataset</h3>
            <div><strong>Project ID:</strong> {contract.big_query_dataset?.project_id}</div>
            <div><strong>Dataset Name:</strong> {contract.big_query_dataset?.data_set_name}</div>
          </div>
          <div className="form-section">
            <h3>Physical Tables</h3>
            {contract.physical_model?.physical_tables?.map((table, tIdx) => (
              <div key={tIdx} style={{border: "1px solid #b3e5fc", padding: "10px", marginBottom: "10px", borderRadius: "6px"}}>
                <div><strong>Table Name:</strong> {table.table_name}</div>
                <div><strong>Table Description:</strong> {table.table_description}</div>
                <div><strong>Partitioning Fields:</strong> {table.partitioning_fields}</div>
                <div><strong>Clustering Fields:</strong> {table.clustering_fields}</div>
                <h4>Physical Fields</h4>
                <table className="physical-fields-table" style={{tableLayout: "fixed", width: "100%"}}>
                  <thead>
                    <tr>
                      <th style={{width: "18%"}}>Field Name</th>
                      <th style={{width: "18%"}}>Field Type</th>
                      <th style={{width: "18%"}}>Field Description</th>
                      <th style={{width: "32%"}}>Nested Fields</th>
                    </tr>
                  </thead>
                  <tbody>
                    {table.physical_fields?.map((field, fIdx) => (
                      <tr key={fIdx}>
                        <td>{field.field_name}</td>
                        <td>{field.field_type}</td>
                        <td>{field.field_description}</td>
                        <td>
                          {field.nested_fields && field.nested_fields.length > 0 ? (
                            <table style={{width: "100%", background: "#f5faff", borderRadius: "6px"}}>
                              <thead>
                                <tr>
                                  <th style={{width: "30%"}}>Nested Field Name</th>
                                  <th style={{width: "30%"}}>Type</th>
                                  <th style={{width: "40%"}}>Description</th>
                                </tr>
                              </thead>
                              <tbody>
                                {field.nested_fields.map((nested, nIdx) => (
                                  <tr key={nIdx}>
                                    <td>{nested.field_name}</td>
                                    <td>{nested.field_type}</td>
                                    <td>{nested.field_description}</td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          ) : (
                            <span style={{color: "#888"}}>—</span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
import React, { useEffect, useState } from "react";
import API_CONFIG from "../config";
import "./../styles.css";

export default function LiquibaseAdmin() {
  const [ids, setIds] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [result, setResult] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetch(API_CONFIG.BASE_URL + "/api/contracts/all-ids")
      .then(res => res.json())
      .then(setIds)
      .catch(err => {
        console.error("Failed to fetch contract IDs:", err);
        setResult("Error: Failed to fetch contract IDs");
      });
  }, []);

  const handleGenerateChangeset = async () => {
    if (!selectedId) {
      setResult("Error: Please select a contract ID");
      return;
    }

    setLoading(true);
    setResult("Generating Liquibase changeset...");

    try {
      const res = await fetch(API_CONFIG.BASE_URL + `/api/liquibase/generateChangelog/${selectedId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" }
      });

      const text = await res.text();
      
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}: ${text}`);
      }

      try {
        const jsonResponse = JSON.parse(text);
        setResult(JSON.stringify(jsonResponse, null, 2));
      } catch {
        setResult(text);
      }
    } catch (err) {
      console.error("Generate changeset error:", err);
      setResult("Error: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleApplyChangeset = async () => {
    if (!selectedId) {
      setResult("Error: Please select a contract ID");
      return;
    }

    setLoading(true);
    setResult("Applying Liquibase changeset...");

    try {
      const res = await fetch(API_CONFIG.BASE_URL + `/api/liquibase/applyChangelog/${selectedId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" }
      });

      const text = await res.text();
      
      if (!res.ok) {
        throw new Error(`HTTP ${res.status}: ${text}`);
      }

      try {
        const jsonResponse = JSON.parse(text);
        setResult(JSON.stringify(jsonResponse, null, 2));
      } catch {
        setResult(text);
      }
    } catch (err) {
      console.error("Apply changeset error:", err);
      setResult("Error: " + err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <h1>Liquibase Admin</h1>
      
      <div className="form-section">
        <h3>Select Data Product</h3>
        <label>Contract ID:</label>
        <select 
          value={selectedId} 
          onChange={e => setSelectedId(e.target.value)}
          disabled={loading}
        >
          <option value="">--Select Contract ID--</option>
          {ids.map(id => (
            <option key={id} value={id}>{id}</option>
          ))}
        </select>
        
        {selectedId && (
          <div style={{ color: '#666', fontSize: '14px', marginTop: '8px' }}>
            Selected Contract ID: {selectedId}
          </div>
        )}
      </div>

      <div className="form-section">
        <h3>Liquibase Operations</h3>
        
        <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
          <button 
            type="button"
            onClick={handleGenerateChangeset}
            disabled={!selectedId || loading}
            style={{
              backgroundColor: '#28a745',
              color: 'white',
              padding: '12px 24px',
              border: 'none',
              borderRadius: '6px',
              cursor: selectedId && !loading ? 'pointer' : 'not-allowed',
              fontSize: '16px',
              fontWeight: '500',
              opacity: selectedId && !loading ? 1 : 0.6,
              transition: 'all 0.2s ease'
            }}
            title={!selectedId ? "Please select a contract ID first" : "Generate Liquibase changeset for the selected contract"}
          >
            {loading ? "Processing..." : "🔧 Generate Liquibase ChangeSet"}
          </button>

          <button 
            type="button"
            onClick={handleApplyChangeset}
            disabled={!selectedId || loading}
            style={{
              backgroundColor: '#007bff',
              color: 'white',
              padding: '12px 24px',
              border: 'none',
              borderRadius: '6px',
              cursor: selectedId && !loading ? 'pointer' : 'not-allowed',
              fontSize: '16px',
              fontWeight: '500',
              opacity: selectedId && !loading ? 1 : 0.6,
              transition: 'all 0.2s ease'
            }}
            title={!selectedId ? "Please select a contract ID first" : "Apply Liquibase changeset for the selected contract"}
          >
            {loading ? "Processing..." : "⚡ Apply Liquibase ChangeSet"}
          </button>
        </div>

        {!selectedId && (
          <div style={{ 
            color: '#ffc107', 
            fontSize: '14px', 
            marginTop: '12px',
            padding: '8px 12px',
            backgroundColor: '#fff3cd',
            border: '1px solid #ffeaa7',
            borderRadius: '4px'
          }}>
            ⚠️ Please select a contract ID to enable Liquibase operations
          </div>
        )}
      </div>

      <div className="form-section">
        <h3>Operation Status</h3>
        {loading && (
          <div style={{ 
            color: '#007bff', 
            fontSize: '16px', 
            textAlign: 'center',
            padding: '16px',
            backgroundColor: '#e3f2fd',
            border: '1px solid #bbdefb',
            borderRadius: '6px',
            margin: '12px 0'
          }}>
            🔄 Processing... Please wait
          </div>
        )}
        
        {result && (
          <div className={result.startsWith("Error") ? "alert-error" : "alert-success"}>
            <pre style={{ 
              whiteSpace: 'pre-wrap', 
              wordWrap: 'break-word',
              maxHeight: '400px',
              overflow: 'auto'
            }}>
              {result}
            </pre>
          </div>
        )}
      </div>

      <div className="form-section">
        <h3>Information</h3>
        <div style={{ 
          backgroundColor: '#e8f5e8', 
          padding: '16px', 
          borderRadius: '8px', 
          border: '1px solid #c3e6c3' 
        }}>
          <h4 style={{ color: '#155724', marginTop: 0 }}>Liquibase Operations:</h4>
          <ul style={{ color: '#155724', lineHeight: '1.6' }}>
            <li><strong>Generate ChangeSet:</strong> Creates a Liquibase changelog based on the data contract schema</li>
            <li><strong>Apply ChangeSet:</strong> Executes the generated changelog to update the database schema</li>
            <li><strong>Prerequisites:</strong> Ensure the selected contract has valid table and field definitions</li>
            <li><strong>Note:</strong> Always generate the changeset before applying it</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
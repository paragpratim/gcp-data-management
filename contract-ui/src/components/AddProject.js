import React, { useState } from "react";
import API_CONFIG from "../config";
import "./../styles.css";

export default function AddProject() {
  const [result, setResult] = useState("");
  const [project, setProject] = useState({
    gcp_project_id: ""
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProject({ ...project, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(API_CONFIG.BASE_URL + "/api/gcp/saveProject", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(project)
      });
      const text = await res.text();
      try {
        setResult(JSON.stringify(JSON.parse(text), null, 2));
      } catch {
        setResult(text);
      }
      // Clear form on successful submission
      if (res.ok) {
        setProject({ gcp_project_id: "" });
      }
    } catch (err) {
      setResult("Error: " + err);
    }
  };

  return (
    <div className="container">
      <h1>Add Project</h1>
      <form onSubmit={handleSubmit}>
        <div className="form-section">
          <h3>Project Information</h3>
          <input 
            type="text" 
            name="gcp_project_id" 
            placeholder="Project ID" 
            value={project.gcp_project_id} 
            onChange={handleChange} 
            required 
          />
        </div>
        <button type="submit">Add Project</button>
      </form>
      {result && (
        <div className={result.startsWith("Error") ? "alert-error" : "alert-success"}>
          {result}
        </div>
      )}
    </div>
  );
}
import React, { useState } from "react";
import API_CONFIG from "../config";
import "./../styles.css";

export default function CreateContract() {
  const [result, setResult] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    const body = {
      contract_name: e.target.contract_name.value,
      version: e.target.version.value,
      owner: e.target.owner.value,
      description: e.target.description.value,
      source: {
        project: e.target.source_project.value,
        bucket: e.target.source_bucket.value,
        file_name_pattern: e.target.file_name_pattern.value,
        file_location_folder: e.target.file_location_folder.value,
        file_format: e.target.file_format.value,
        file_frequency: e.target.file_frequency.value
      },
      target: {
        project: e.target.target_project.value,
        bigquery_dataset: e.target.bigquery_dataset.value,
        bigquery_table: e.target.bigquery_table.value
      }
    };
    try {
      const res = await fetch(API_CONFIG.BASE_URL + "/api/contracts/save", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
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
      <h1>Create Contract</h1>
      <form onSubmit={handleSubmit}>
        {/* Contract Info */}
        <h3>Contract Info</h3>
        <input type="text" name="contract_name" placeholder="Contract Name" required />
        <input type="text" name="version" placeholder="Version" required />
        <input type="text" name="owner" placeholder="Owner" required />
        <input type="text" name="description" placeholder="Description" required />
        {/* Source */}
        <h3>Source</h3>
        <input type="text" name="source_project" placeholder="Source Project" required />
        <input type="text" name="source_bucket" placeholder="Source Bucket" required />
        <input type="text" name="file_name_pattern" placeholder="File Name Pattern" required />
        <input type="text" name="file_location_folder" placeholder="File Location Folder" required />
        <input type="text" name="file_format" placeholder="File Format" required />
        <input type="text" name="file_frequency" placeholder="File Frequency" required />
        {/* Target */}
        <h3>Target</h3>
        <input type="text" name="target_project" placeholder="Target Project" required />
        <input type="text" name="bigquery_dataset" placeholder="BigQuery Dataset" required />
        <input type="text" name="bigquery_table" placeholder="BigQuery Table" required />
        <button type="submit">Submit Contract</button>
      </form>
      <pre>{result}</pre>
    </div>
  );
}
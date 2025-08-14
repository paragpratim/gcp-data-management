import React, { useEffect, useState } from "react";
import API_CONFIG from "../config";
import "./../styles.css";

export default function UpdateContract() {
  const [ids, setIds] = useState([]);
  const [selectedId, setSelectedId] = useState("");
  const [form, setForm] = useState(null);
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
      .then(data => setForm(data));
  };

  const handleChange = e => {
    const { name, value } = e.target;
    if (name.startsWith("source_")) {
      setForm({ ...form, source: { ...form.source, [name.replace("source_", "")]: value } });
    } else if (name.startsWith("target_")) {
      setForm({ ...form, target: { ...form.target, [name.replace("target_", "")]: value } });
    } else {
      setForm({ ...form, [name]: value });
    }
  };

  const handleSubmit = async e => {
    e.preventDefault();
    try {
      const res = await fetch(API_CONFIG.BASE_URL + "/api/contracts/update", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form)
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
      <h1>Update Contract</h1>
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
      {form && (
        <form onSubmit={handleSubmit}>
          <h3>Edit Contract</h3>
          <input type="text" name="contract_name" value={form.contract_name} onChange={handleChange} required />
          <input type="text" name="version" value={form.version} onChange={handleChange} required />
          <input type="text" name="owner" value={form.owner} onChange={handleChange} required />
          <input type="text" name="description" value={form.description} onChange={handleChange} required />
          <h3>Source</h3>
          <input type="text" name="source_project" value={form.source.project} onChange={handleChange} required />
          <input type="text" name="source_bucket" value={form.source.bucket} onChange={handleChange} required />
          <input type="text" name="source_file_name_pattern" value={form.source.file_name_pattern} onChange={handleChange} required />
          <input type="text" name="source_file_location_folder" value={form.source.file_location_folder} onChange={handleChange} required />
          <input type="text" name="source_file_format" value={form.source.file_format} onChange={handleChange} required />
          <input type="text" name="source_file_frequency" value={form.source.file_frequency} onChange={handleChange} required />
          <h3>Target</h3>
          <input type="text" name="target_project" value={form.target.project} onChange={handleChange} required />
          <input type="text" name="target_bigquery_dataset" value={form.target.bigquery_dataset} onChange={handleChange} required />
          <input type="text" name="target_bigquery_table" value={form.target.bigquery_table} onChange={handleChange} required />
          <button type="submit">Save Changes</button>
        </form>
      )}
      <pre>{result}</pre>
    </div>
  );
}
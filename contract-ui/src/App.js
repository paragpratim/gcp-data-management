import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import CreateContract from "./components/CreateContract";
import ViewContract from "./components/ViewContract";
import UpdateContract from "./components/UpdateContract";
import AddProject from "./components/AddProject";
import "./styles.css";

function App() {
  return (
    <Router>
      <div className="container">
        <div className="logo">
          <img src="https://cdn-icons-png.flaticon.com/512/5968/5968705.png" alt="Logo" />
        </div>
        <h1>Data Contract Manager</h1>
        <nav style={{ marginBottom: "32px" }}>
          <Link to="/" className="ui-button ui-widget ui-corner-all">Create Contract</Link>
          <Link to="/view" className="ui-button ui-widget ui-corner-all">View Contract</Link>
          <Link to="/update" className="ui-button ui-widget ui-corner-all">Update Contract</Link>
          <Link to="/add-project" className="ui-button ui-widget ui-corner-all">Add Project</Link>
        </nav>
        <Routes>
          <Route path="/" element={<CreateContract />} />
          <Route path="/view" element={<ViewContract />} />
          <Route path="/update" element={<UpdateContract />} />
          <Route path="/add-project" element={<AddProject />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
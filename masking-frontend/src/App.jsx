import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import GetSchema from "./GetSchema";
import SchemaMasking from "./SchemaMasking";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/schema" replace />} />
        <Route path="/schema" element={<GetSchema />} />
        <Route path="/maskSchema" element={<SchemaMasking />} />
      </Routes>
    </Router>
  );
}

export default App;
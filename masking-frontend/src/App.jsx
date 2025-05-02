import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import GetSchema from "./GetSchema";
import SchemaMasking from "./SchemaMasking";
import HomePage from "./HomePage";
import EditConfigPage from "./EditConfigPage";
import PerformMask from "./PerformMask";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/*" element={<Navigate to="/home" replace />} />
        <Route path="/home" element={<HomePage />} />
        <Route path="/schema" element={<GetSchema />} />
        <Route path="/maskSchema" element={<SchemaMasking />} />
        <Route path="/editConfig" element={<EditConfigPage />} />
        <Route path="/performMask" element={<PerformMask />} />
      </Routes>
    </Router>
  );
}

export default App;
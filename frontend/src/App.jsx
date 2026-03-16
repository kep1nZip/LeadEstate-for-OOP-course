import { useEffect, useState } from "react";

function App() {

  const [message, setMessage] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/api/hello")
      .then(res => res.text())
      .then(data => setMessage(data));
  }, []);

  return (
    <div>
      <h1>LeadEstate</h1>
      <p>Response dari backend:</p>
      <h2>{message}</h2>
    </div>
  );
}

export default App;
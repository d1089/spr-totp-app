import { useState } from "react";
import reactLogo from "./assets/react.svg";
import viteLogo from "/vite.svg";
import axios from "axios";
import "./App.css";

function App() {
  const [count, setCount] = useState(0);
  const [key, setKey] = useState("");
  const [qr, setQR] = useState("");
  const [email, setEmail] = useState("");
  const [acn, setACN] = useState("");
  const [inputValue, setInputValue] = useState("");
  const [ver, setVer] = useState("");

  // const handleInputChange = (event) => {
  //   setInputValue(event.target.value);
  // };

  const handleSubmit = (event) => {
    event.preventDefault();
    axios
      .get("http://localhost:8080/verTOTP?totpCode=" + inputValue)
      .then(function (response) {
        // handle success
        if (response.data.length > 0) setVer("Verified");
        else setVer("Verification Failed");
        console.log(response.data);
      })
      .catch(function (error) {
        // handle error
        console.error(error);
      });
  };

  const genKey = () => {
    console.log("under genKey");
    axios
      .get("http://localhost:8080/genKey")
      .then(function (response) {
        // handle success
        if (response.data.length > 0) setKey(response.data);
        console.log(response.data);
      })
      .catch(function (error) {
        // handle error
        console.error(error);
      });
  };

  const genQR = () => {
    console.log("under genKey");
    axios
      .get(
        "http://localhost:8080/genQR?email=" +
          email +
          "&secretKey=" +
          key +
          "&accountName=" +
          acn
      )
      .then(function (response) {
        // handle success
        if (response.data.length > 0) setQR(response.data);
        console.log(response.data);
      })
      .catch(function (error) {
        // handle error
        console.error(error);
      });
  };

  return (
    <>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "space-between",
          height: "300px",
        }}
      >
        <div>
          <h2>{key}</h2>
          <button onClick={() => genKey()}>Gen Key</button>
        </div>
        <div style={{ display: "flex", flexDirection: "column" }}>
          <h5>Email</h5>
          <input
            type="text"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            title="Email"
          />
          <h5>Account Name</h5>
          <input
            type="text"
            value={acn}
            onChange={(e) => setACN(e.target.value)}
            title="Account Name"
          />
          <img
            src={qr}
            alt="base64 encoded"
            style={{ display: qr.length > 0 ? "block" : "none" }}
          ></img>
          <button onClick={() => genQR()}>Gen QR</button>
        </div>
        <div>
          <form onSubmit={handleSubmit}>
            <input
              type="text"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
            />
            <button type="submit">Verify</button>
          </form>
        </div>
      </div>
    </>
  );
}

export default App;

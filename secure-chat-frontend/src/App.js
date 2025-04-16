// react_diffie_hellman_chat/src/App.jsx
import React, { useEffect, useState } from 'react';
import './App.css';
import * as crypto from 'crypto-browserify';
import axios from 'axios';

const App = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [jwtToken, setJwtToken] = useState('');
  const [sharedSecret, setSharedSecret] = useState(null);

  const dh = crypto.createDiffieHellman(2048);
  const myPublicKey = dh.generateKeys('base64');
  const myPrivateKey = dh.getPrivateKey();

  useEffect(() => {
    const token = localStorage.getItem('jwt') || 'your_jwt_token_here';
    setJwtToken(token);

    // Exchange public keys
    axios.post('http://localhost:8080/exchange', {
      userId: 'alice',
      publicKey: myPublicKey,
    }, {
      headers: { Authorization: `Bearer ${token}` },
    }).then(res => {
      const otherPublicKey = res.data.otherPublicKey;
      const secret = dh.computeSecret(Buffer.from(otherPublicKey, 'base64'));
      setSharedSecret(secret);
      console.log('Shared secret established');
    });

    // Fetch past messages
    axios.get('http://localhost:8080/messages', {
      headers: { Authorization: `Bearer ${token}` },
    }).then(res => {
      const decryptedMessages = res.data.map((msg) => {
        try {
          const decipher = crypto.createDecipheriv('aes-256-gcm', sharedSecret?.slice(0, 32), Buffer.alloc(12, 0));
          let decrypted = decipher.update(msg, 'base64', 'utf8');
          decrypted += decipher.final('utf8');
          return `Bob: ${decrypted}`;
        } catch (e) {
          return 'Decryption error';
        }
      });
      setMessages(decryptedMessages);
    });
  }, []);

  const sendMessage = async () => {
    if (!sharedSecret || !input) return;

    const cipher = crypto.createCipheriv('aes-256-gcm', sharedSecret.slice(0, 32), Buffer.alloc(12, 0));
    let encrypted = cipher.update(input, 'utf8', 'base64');
    encrypted += cipher.final('base64');

    await axios.post('http://localhost:8080/send', {
      to: 'bob',
      message: encrypted,
    }, {
      headers: { Authorization: `Bearer ${jwtToken}` },
    });

    setMessages((prev) => [...prev, `You: ${input}`]);
    setInput('');
  };

  return (
    <div className="App">
      <h2>Secure Chat (HTTPS + DH)</h2>
      <div className="chat-box">
        {messages.map((msg, idx) => <div key={idx}>{msg}</div>)}
      </div>
      <input
        type="text"
        value={input}
        onChange={(e) => setInput(e.target.value)}
        placeholder="Type a message"
      />
      <button onClick={sendMessage}>Send</button>
    </div>
  );
};

export default App;

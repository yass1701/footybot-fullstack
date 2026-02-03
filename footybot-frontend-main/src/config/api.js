// Shared API configuration
// Falls back to Render backend if REACT_APP_API_URL is not set or points to localhost
const API_BASE = (process.env.REACT_APP_API_URL && !process.env.REACT_APP_API_URL.includes('localhost'))
    ? process.env.REACT_APP_API_URL
    : 'https://footybot-backend.onrender.com';

export default API_BASE;

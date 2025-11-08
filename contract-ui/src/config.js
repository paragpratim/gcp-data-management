const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_URL || 'http://localhost:8080', // Use relative URLs, nginx will proxy to backend
};

export default API_CONFIG;
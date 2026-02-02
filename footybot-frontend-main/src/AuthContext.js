import React, { createContext, useState, useContext } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem('authToken'));
    // Add state for roles, reading from localStorage
    const [roles, setRoles] = useState(JSON.parse(localStorage.getItem('userRoles')) || []);
    const [username, setUsername] = useState(localStorage.getItem('username') || '');

    const login = (newToken, userRoles, userUsername) => {
        localStorage.setItem('authToken', newToken);
        localStorage.setItem('userRoles', JSON.stringify(userRoles)); // Save roles as a string
        localStorage.setItem('username', userUsername); // Save username
        setToken(newToken);
        setRoles(userRoles);
        setUsername(userUsername);
    };

    const logout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRoles'); // Remove roles on logout
        localStorage.removeItem('username'); // Remove username on logout
        setToken(null);
        setRoles([]);
        setUsername('');
    };

    const value = { token, roles, username, login, logout };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
    return useContext(AuthContext);
};
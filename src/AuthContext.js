// AuthContext.js
import React, {createContext, useContext, useEffect, useRef, useState} from 'react';
import {Stomp} from "@stomp/stompjs";
import axios from "axios";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const client = useRef(null);
    const [socketMessage, setSocketMessage] = useState([]);

    useEffect(() => {
        if (user) {
            const chatWebsocket = new WebSocket("ws://192.168.3.93:8787/chat")
            client.current = Stomp.over(chatWebsocket)
            client.current.connect({},() => {
                client.current.subscribe(`/chat?userId=${user.username}`, (payload) => {
                const message = JSON.parse(payload.body)
                setSocketMessage(prevMessage => [...prevMessage, message])

                })
            })
        } else {
            if (client.current) {
                client.current.disconnect();
                setSocketMessage([]);
            }
        }

        return () => {
            if (client.current) {
                client.current.disconnect();
                setSocketMessage([]);
            }

        }
    }, [user]);

    const login = (userData) => {
        setUser(userData);
    };

    const logout = () => {
        axios.get(`http://192.168.3.93:8787/apis/member/logout`, {withCredentials: true})
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, client, socketMessage }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};

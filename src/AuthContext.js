// AuthContext.js
import React, {createContext, useContext, useEffect, useRef, useState} from 'react';
import {Stomp} from "@stomp/stompjs";
import axios from "axios";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const client = useRef(null);
    const clientNotification = useRef(null);
    const [socketMessage, setSocketMessage] = useState([]);
    const [notificationMessage, setNotificationMessage] = useState([]);


    useEffect(() => {
        const storeUser = localStorage.getItem('user');
        if (storeUser) {
            setUser(JSON.parse(storeUser));
        }
    }, []);

    useEffect(() => {
        if (user) {
            localStorage.setItem('user', JSON.stringify(user));
            const chatWebsocket = new WebSocket("ws://192.168.3.93:8787/chat")
            client.current = Stomp.over(chatWebsocket)
            client.current.connect({},() => {
                client.current.subscribe(`/chat?userId=${user.username}`, (payload) => {
                const message = JSON.parse(payload.body)
                setSocketMessage(prevMessage => [...prevMessage, message])

                })
            })
            if (client.current) {
                notificationSubscribe(client.current);
            }
        } else {
            const handleBeforeUnload = () => {
                if (client.current) {
                    client.current.disconnect();
                    setSocketMessage([]);
                }
            }
            window.addEventListener('beforeunload', handleBeforeUnload);
        }
    }, [user]);

    const notificationSubscribe = (current) => {
        if (user && current) {
            const notificationWebSocket = new WebSocket("ws://192.168.3.93:8787/notification")
            clientNotification.current = Stomp.over(notificationWebSocket);
            clientNotification.current.connect({}, () => {
                clientNotification.current.subscribe(`/notification?userId=${user.username}`, (payload) => {
                    const notificationMessage = JSON.parse(payload.body)
                    setNotificationMessage(prevMessage => [...prevMessage, notificationMessage])
                })
            })
        } else {
            const handleBeforeUnload = () => {
                if (!client.current) {
                    clientNotification.current.disconnect();
                    setNotificationMessage([]);
                }
            }
            window.addEventListener('beforeunload', handleBeforeUnload);
        }
    }

    const login = (userData) => {
        setUser(userData);
    };

    const logout = () => {
        axios.get(`http://192.168.3.93:8787/apis/member/logout`, {withCredentials: true})
        localStorage.removeItem('user');
        setUser(null);
        if (client.current) {
            client.current.disconnect();
            setSocketMessage([]);
        }
        if (!client.current) {
            clientNotification.current.disconnect();
            setNotificationMessage([])
        }
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, client, clientNotification, socketMessage, notificationMessage }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};

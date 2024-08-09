// AuthContext.js
import React, {createContext, useContext, useEffect, useRef, useState} from 'react';
import {Stomp} from "@stomp/stompjs";
import axios from "axios";
import { initializeApp } from "firebase/app";
import { getMessaging, getToken, onMessage } from "firebase/messaging";



const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const client = useRef(null);
    const clientNotification = useRef(null);
    const [socketMessage, setSocketMessage] = useState([]);
    const [notificationMessage, setNotificationMessage] = useState([]);

    function getDeviceInfo(deviceInfo) {
        if (/android/i.test(deviceInfo)) {
            return 'Mobile';
        } else if (/iPad|iPhone|iPod/.test(deviceInfo) && !window.MSStream) {
            return 'Mobile';
        } else if (/Windows|Macintosh|Linux/.test(deviceInfo)) {
            return 'PC';
        } else {
            return 'Unknown';
        }
    }
    function getDeviceType(deviceInfo) {
        if (/android/i.test(deviceInfo)) {
            return 'Android';
        } else if (/iPad|iPhone|iPod/.test(deviceInfo) && !window.MSStream) {
            return 'Ios';
        } else if (/Windows|Macintosh|Linux/.test(deviceInfo)) {
            return 'Browser';
        } else {
            return 'Unknown';
        }
    }
    const deviceInfo = getDeviceInfo(navigator.userAgent);
    const deviceType = getDeviceType(navigator.userAgent);


    useEffect(() => {
        const storeUser = localStorage.getItem('user');
        if (storeUser) {
            setUser(JSON.parse(storeUser));
        }
    }, []);

    useEffect(() => {
        if (user) {
            localStorage.setItem('user', JSON.stringify(user));
            const chatWebsocket = new WebSocket("ws://localhost:8787/chat")
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
                    notificationSubscribe(null)
                }
            }
            window.addEventListener('beforeunload', handleBeforeUnload);
        }
    }, [user]);

    const notificationSubscribe = (current) => {
        if (user && current != null) {
            const notificationWebSocket = new WebSocket("ws://localhost:8787/notification")
            clientNotification.current = Stomp.over(notificationWebSocket);
            clientNotification.current.connect({}, () => {
                clientNotification.current.subscribe(`/notification?userId=${user.username}`, (payload) => {
                    const notificationMessages = JSON.parse(payload.body)
                    setNotificationMessage(prevMessage => [...prevMessage, notificationMessages])
                })
            });

            if ('serviceWorker' in navigator) {
                navigator.serviceWorker.register('/firebase-messaging-sw.js')
                    .then((registration) => {
                        console.log('Service Worker registered with scope:', registration.scope);
                    })
                    .catch((err) => {
                        console.error('Service Worker registration failed:', err);
                    });
            } else {
                console.warn('Service Workers are not supported in this browser.');
            }


            const firebaseConfig = {
                apiKey: "AIzaSyD8ncGQ_vOqvtRKZGoUlaH7iJd5PI5sDMM",
                authDomain: "test-project-97073.firebaseapp.com",
                projectId: "test-project-97073",
                storageBucket: "test-project-97073.appspot.com",
                messagingSenderId: "677608979829",
                appId: "1:677608979829:web:6ba8cf49fa53920e5fe73c",
                measurementId: "G-ME1ZSK82X8"
            };

             const app = initializeApp(firebaseConfig);
             const messaging = getMessaging(app);

            async function requestPermission() {
                console.log("권한 요청 중...");

                const permission = await Notification.requestPermission();
                if (permission === "denied") {
                    console.log("알림 권한 허용 안됨");
                    return;
                }
                console.log("알림권한 허용")

                const token = await getToken(messaging, {
                    vapidKey: 'BBv_KcV66mSaaOZXXKCf834LZs9lQlTA4RN1ejzNRBia8LaY2LLJlT5LbECF94MtY7xmv1RPv8lxs8THV5HwMP0',
                });
                console.log("토큰 받아온다")
                if (token) {
                    const data = {
                        memberUid : user.username,
                        fcmToken : token,
                        deviceType : deviceType,
                        deviceInfo : deviceInfo
                    }
                    axios.post(`http://localhost:8787/apis/fcm`, data, {withCredentials: true})
                        .then(response => {
                            if (response.data === 'success') {
                                console.log('저장성공')
                            } else {
                                console.log('저장실패')
                            }
                        })
                    } else {
                        console.log("Can not get Token");
                    }

                onMessage(messaging, (payload) => {
                    console.log("메시지가 도착했습니다.", payload);

                });
            }
            requestPermission();
        } else {
            const data = {
                memberUid : user.username,
                deviceType : deviceType,
                deviceInfo : deviceInfo
            }
            axios.put(`http://localhost:8787/apis/fcm`, data, {withCredentials: true})
                .then(response => {
                    if(response.data === 'success') {
                        console.log('connectedYn => N')
                    } else {
                        console.log('실패')
                    }
                })
            const handleBeforeUnload = () => {
                if (!client.current) {
                    clientNotification.current.disconnect();
                    setNotificationMessage([]);
                    console.log('연결 끊긴다.')
                }
            }
            window.addEventListener('beforeunload', handleBeforeUnload);
        }
    }

    const login = (userData) => {
        setUser(userData);
    };

    const logout = () => {
        axios.get(`http://localhost:8787/apis/member/logout`, {withCredentials: true})
        localStorage.removeItem('user');
        setUser(null);
        if (client.current) {
            client.current.disconnect();
            setSocketMessage([]);
            setNotificationMessage([])
        }
        if (!client.current) {
            clientNotification.current.disconnect();
            console.log('연결 끊김')
            setSocketMessage([]);
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

import React, {useEffect, useRef, useState} from 'react';
import {Link, useNavigate} from "react-router-dom";
import { useAuth } from "./AuthContext";
import './header.css';
import {Stomp} from "@stomp/stompjs";

function Header(props) {
    const {user, logout, socketMessage, notificationMessage} = useAuth();
    const navigate = useNavigate();
    const [messages, setMessages] = useState([]);
    const [showDropdown, setShowDropdown] = useState(false);
    const [lastMessage, setLastMessage] = useState('');
    const {clientNotification} = useAuth();
    const savedCallBack = useRef();

    useEffect(() => {
        if (user) {
            const eventSource = new EventSource(`http://192.168.3.93:8787/apis/notifications/subscribe/${user.username}`, {withCredentials: true});
            try {
                eventSource.onopen = () => {
                    console.log('EventSources ReadyState', eventSource.readyState);
                    console.log('EventSources url', eventSource.url);
                    console.log('Open Event');
                };
                eventSource.addEventListener('boardArticle', (event) => {
                    console.log('connect');
                    setMessages((prevMessages) => [...prevMessages, event.data]);
                });
            } catch (err) {
                eventSource.onerror = (error) => {
                    console.log(error);
                };
            }
            return () => {
                if (eventSource) {
                    eventSource.close();
                }
            }
        }
    }, [user]);

    useEffect(() => {
        console.log(clientNotification)
        console.log(user)
        if (user && clientNotification.current) {
            const heartBeat = setInterval(() => {
                const message = "PING";
                clientNotification.current.send(`/pub/notification/heartbeat`, {}, message)
            }, 10000);
            return () => clearInterval(heartBeat);
        }
    }, []);

    const handlerMain = () => {
        logout();
        navigate('/')
    }

    useEffect(() => {
        isMyMessage()
    }, [notificationMessage]);

    const isMyMessage = () => {
        if (user) {
            if (!notificationMessage || !notificationMessage.length) {
                return false;
            }
            const lastMessage = notificationMessage[notificationMessage.length - 1];
            if (!lastMessage || !lastMessage.userList) {
                return false;
            }

            const isUserInclueded = lastMessage.userList.includes(user.username);
            return isUserInclueded
        }
    }

    return (
        <div className='header-container'>
            <Link to="/">홈</Link>
            <div className='user-info'>
                <li style={{backgroundColor: isMyMessage() ? 'green' : 'red'}}></li>

                {user && <span>{user.username} 님 </span>}
                {user && <button onClick={handlerMain}>로그아웃</button>}
                {user && <Link to={"/login"}></Link>}
            </div>
            {user && (
                <div className='dropdown' onClick={() => setShowDropdown(!showDropdown)}>
                    <span>({messages.length})</span>
                    {showDropdown && (
                        <ul>
                            {messages.map((messages, index) => (
                                <li key={index}>{messages}</li>
                            ))}
                        </ul>
                    )}
                </div>
            )}
        </div>

    );
}

export default Header;
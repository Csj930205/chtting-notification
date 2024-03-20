import React, {useEffect, useRef, useState} from 'react';
import {Link, useNavigate} from "react-router-dom";
import { useAuth } from "./AuthContext";
import './header.css';
import {Stomp} from "@stomp/stompjs";
import axios from "axios";

function Header(props) {
    const {user, logout, socketMessage, notificationMessage} = useAuth();
    const navigate = useNavigate();
    const [messages, setMessages] = useState([]);
    const [showDropdown, setShowDropdown] = useState(false);
    const [count, setConut] = useState(0);
    const [notification, setNotification] = useState([]);

    // useEffect(() => {
    //     if (user) {
    //         const eventSource = new EventSource(`http://192.168.3.93:8787/apis/notifications/subscribe/${user.username}`, {withCredentials: true});
    //         try {
    //             eventSource.onopen = () => {
    //                 console.log('EventSources ReadyState', eventSource.readyState);
    //                 console.log('EventSources url', eventSource.url);
    //                 console.log('Open Event');
    //             };
    //             eventSource.addEventListener('boardArticle', (event) => {
    //                 console.log('connect');
    //                 setMessages((prevMessages) => [...prevMessages, event.data]);
    //             });
    //         } catch (err) {
    //             eventSource.onerror = (error) => {
    //                 console.log(error);
    //             };
    //         }
    //         return () => {
    //             if (eventSource) {
    //                 eventSource.close();
    //             }
    //         }
    //     }
    // }, [user]);

    useEffect(() => {
        if (user) {
            const memberUid = user.username
            axios.get(`http://192.168.3.93:8787/apis/unread/notification/${memberUid}`, {withCredentials: true})
                .then((res) => {
                    setConut(res.data.count)
                })
        }
    }, [user]);

    const handlerMain = () => {
        logout();
        navigate('/')
    }

    useEffect(() => {
        isMyMessage()
        if (notificationMessage && notificationMessage.length > 0) {
            const filterMessages = notificationMessage.filter(message => message.memberUid);
            setNotification(filterMessages);
        } else {
            setNotification([]);
        }
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

    const handleCount = () => {
        if(user) {
            const memberUid = user.username
            axios.delete(`http://192.168.3.93:8787/apis/unread/${memberUid}`, {withCredentials: true})
                .then((res) => {
                    setConut(0);
                })
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
                    <span>방금온 메시지: ({notification.length})</span>
                    {showDropdown && (
                        <ul>
                            {notification.map((messages, index) => (
                                <li key={index}>{messages.message}</li>
                            ))}
                        </ul>
                    )}
                    <br/>
                    <span onClick={handleCount}>읽지않은 메시지: ({count})</span>
                </div>
            )}
        </div>

    );
}

export default Header;
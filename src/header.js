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
    const [showUnreadDropdown, setShowUnreadDropdown] = useState(false);
    const [count, setConut] = useState(0);
    const [notification, setNotification] = useState([]);
    const [modal ,setModal] = useState(false)
    const [unreadNotification, setUnreadNotification] = useState([]);
    const [showAllList, setShowAllList] = useState(false);
    const [notificationAllList, setNotificationAllList] = useState([])

    // useEffect(() => {
    //     if (user) {
    //         const eventSource = new EventSource(`http://localhost:8787/apis/notifications/subscribe/${user.username}`, {withCredentials: true});
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
            axios.get(`http://localhost:8787/apis/unread/notification/${memberUid}`, {withCredentials: true})
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
            axios.delete(`http://localhost:8787/apis/unread/${memberUid}`, {withCredentials: true})
                .then((res) => {
                    setConut(0);
                    setShowUnreadDropdown(true)
                    setUnreadNotification(res.data.unreadNotification)
                })
        } else {
            setConut(0)
            setShowUnreadDropdown(false)
            setUnreadNotification([])
        }
    }
    const handleAllNotification = () => {
        if (user) {
            axios.get(`http://localhost:8787/apis/list`, {withCredentials: true})
                .then((res) => {
                    setShowAllList(true);
                    setNotificationAllList(res.data);
                })
        } else {
            setShowAllList(false)
            setNotificationAllList([])
        }
    }

    return (
        <div className='header-container'>
            <Link to="/">홈</Link>
            <div className='user-info'>
                {user && <li style={{backgroundColor: isMyMessage() ? 'green' : 'red'}}></li>}
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
                    {showUnreadDropdown && (
                        <ul>
                            {unreadNotification.map((messages, index) => (
                                <li key={index}>{messages.message}</li>
                            ))}
                        </ul>
                    )}
                    <br/>
                    <span onClick={handleAllNotification}>전체 알림보기</span>
                    {showAllList && (
                        <ul>
                            {notificationAllList.map((messages, index) => (
                                <li key={index}>{messages.message}</li>
                            ))}
                        </ul>
                    )}
                </div>
            )}
        </div>

    );
}

export default Header;
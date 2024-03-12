import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { EventSourcePolyfill, NativeEventSource } from 'event-source-polyfill';
import { useNavigate } from 'react-router-dom';
import { useAuth } from "../AuthContext";

function Sse(props) {
    const [messages, setMessages] = useState([]);
    const [addMessages, setAddMessages] = useState([]);
    const [uuid, setUuid] = useState('');
    const [showDropdown, setShowDropdown] = useState(false); // 드롭다운 표시 여부 상태 추가
    const EventSource = NativeEventSource || EventSourcePolyfill;
    const navigate = useNavigate();
    const { user } = useAuth();

    console.log(user)
    const connect = () => {
        const eventSource = new EventSource(`http://192.168.3.93:8787/apis/notifications/subscribe/${user.username}`, {
            withCredentials: true,
        });
        try {
            eventSource.onopen = () => {
                console.log('EventSources ReadyState', eventSource.readyState);
                console.log('EventSources url', eventSource.url);
                console.log('Open Event');
            };
            eventSource.addEventListener('connect', (event) => {
                console.log('connect');
                setMessages((prevMessages) => [...prevMessages, event.data]);
            });
            eventSource.addEventListener('boardArticle', (event) => {
                console.log('addMessage');
                setAddMessages((prevMessages) => [...prevMessages, event.data]);
                setShowDropdown(true); // 이벤트 발생 시 드롭다운 표시
            });
        } catch (err) {
            eventSource.onerror = (error) => {
                console.log(error);
            };
        }
    };

    const send = () => {
        axios.get(`http://192.168.3.93:8787/apis/test/${user.username}`, { withCredentials: true }).then((res) => {
            console.log(res.data);
        });
    };

    const list = () => {
        axios.get(`http://192.168.3.93:8787/apis/list`, { withCredentials: true });
    };

    const redirect = () => {
        navigate('/testChatting');
    };

    const redirectBoardGroup = () => {
        navigate('/board-group')
    }

    const memberChattingRoom = () => {
        navigate('/member-chatting')
    }

    return (
        <div>
            <br/>
            <button onClick={redirect}>테스트 채팅방 이동</button>
            <br/>
            <br/>
            <button onClick={memberChattingRoom}>회원채팅방으로 이동</button>
            <br/>
            <br/>
            <button onClick={list}>데이터 받아오기</button>
            <br/>
            <br/>
            <button onClick={redirectBoardGroup}>게시판 그룹으로 이동</button>

            {/* 드롭다운 영역 */}
            {showDropdown && (
                <div>
                    <p>새로운 메시지가 도착했습니다!</p>
                    <button onClick={() => setShowDropdown(false)}>닫기</button>
                </div>
            )}
            <ul>
                {messages.map((message, index) => (
                    <li key={index}>{message}</li>
                ))}
                {addMessages.map((addMessage, index) => (
                    <li key={index}>{addMessage}</li>
                ))}
            </ul>
        </div>
    );
}

export default Sse;
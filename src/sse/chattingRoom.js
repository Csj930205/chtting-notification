import React, {useEffect, useRef, useState} from 'react';
import {useLocation, useNavigate, useParams} from "react-router-dom";
import {Stomp} from "@stomp/stompjs";
import axios from "axios";

function ChattingRoom(props) {
    const location = useLocation();
    const navigate = useNavigate();
    const client = useRef(null);
    const [inputValue, setInputValue] = useState('');
    const [messages, setMessages] = useState([]);
    const uid = location.state?.uid;

    useEffect(() => {
        const chatWebSocket = new WebSocket("ws://192.168.3.93:8787/chat")
        client.current = Stomp.over(chatWebSocket);
        client.current.connect({}, () => {
            client.current.subscribe(`/chat/room/${uid}`, (payload) => {
                const chat = JSON.parse(payload.body)
                setMessages(prevMessage => [...prevMessage, chat])
            })
            console.log('Connected to WebSocket')
        });
        return () => {
            console.log('UnConnected chat to WebSocket');
            client.current.disconnect();
            chatWebSocket.close();
        }

    }, []);

    useEffect(() => {
        axios.get(`http://192.168.3.93:8787/apis/chatting-list/${uid}`)
            .then(response => {
                console.log(response.data)
                setMessages(prevMessages => [...prevMessages, ...response.data])
            })
    }, [uid]);

    const handleInputValue = (e) => {
        setInputValue(e.target.value);
    }

    const redirectChattingRoomList = () => {
        navigate('/chatting');
    }

    const sendMessage = (e) => {
        e.preventDefault();

        if (inputValue.trim() === '') {
            alert('메세지를 입력해 주세요')
            return;
        }
        if (!client.current.connected) {
            alert('연결이 불안정합니다. 다시 시도해 주세요')
            return;
        }
        const data = {
            roomUid: uid,
            content: inputValue,
            memberUid: '62d78a901224596285245011'
        }
        client.current.send(`/pub/chat/send-message`, {}, JSON.stringify(data));
        setInputValue('')
    }
    return (
        <div>
            채팅
            <div className='chat-container'>
                <input type="text" value={inputValue} onChange={handleInputValue} />
                <button onClick={sendMessage}>
                    클릭
                </button>
                <button onClick={redirectChattingRoomList}>
                    나가기
                </button>
                {messages.map((message, index) => (
                    <div key={index}>
                        <span>아이디: {message.roomUid}</span>
                        <br/>
                        <span>사용자 식별자: {message.memberUid}</span>
                        <br/>
                        <span>내용: {message.content}</span>
                        <br/>
                        <span>시간: {message.timestamp}</span>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default ChattingRoom;
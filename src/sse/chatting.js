import React, {useEffect, useRef, useState} from 'react';
import {useNavigate} from "react-router-dom";
import axios from "axios";
import {useAuth} from "../AuthContext";
import {Stomp} from "@stomp/stompjs";

function Chatting(props) {
    const navigate = useNavigate();
    const [chattingRoomList, setChattingRoomList] = useState([]);
    const [memberList, setMemberList] = useState([]);
    const [messages, setMessages] = useState([]);
    const myClient = useRef(null);
    const userClient = useRef({});
    const [selectedUser, setSelectedUser] = useState(null);
    const {user} = useAuth();
    const [inputMessage, setInputMessage] = useState('');

    const handleInputMessage = (e) => {
        setInputMessage(e.target.value);
    }

    const handleSendMessage = (e) => {
        setInputMessage('');
        sendMessage(e);
    }

    const sendMessage = (e) => {
        e.preventDefault()

        if (inputMessage.trim() === '') {
            alert('메세지를 입력해 주세요.')
            return;
        }
        if (!userClient.current.connected) {
            alert('연결이 불안정 합니다.')
            return;
        }

        const data = {
            roomUid: selectedUser,
            content: inputMessage
        }
        userClient.current.send(`/pub/chat/send-message`, {}, JSON.stringify(data));
    }

    useEffect(() => {
        axios.get(`http://192.168.3.93:8787/apis/chatting-room`, {withCredentials: true})
            .then(res => {
                setChattingRoomList(res.data.chattingRoomList);
            })
            .catch(error => {
                console.error(error)
            })
        axios.get(`http://192.168.3.93:8787/apis/member`, {withCredentials: true})
            .then(res => {
                setMemberList(res.data)
            })
            .catch(error => {
                console.error(error)
            })
        const chatWebsocket = new WebSocket("ws://192.168.3.93:8787/chat")
            myClient.current = Stomp.over(chatWebsocket)
            myClient.current.connect({}, () => {
                myClient.current.subscribe(`/chat/room/${user.username}`, (payload) => {
                    const chat = JSON.parse(payload.body)
                    setMessages(prevMessage => [...prevMessage, chat])
                })
            })
            return () => {
                myClient.current.disconnect();
                chatWebsocket.close();
            }
    }, []);

    const redirect = () => {
        navigate('/')
    }
    const createRoom = () => {
        const participants = '6281d7b29c6d287b4b67303e'
        const createdBy = '62d78a901224596285245011'
        const createdDate = new Date()
        const data = {
            participants: participants,
            createdBy : createdBy,
            createdDate : createdDate
        }
        axios.post(`http://192.168.3.93:8787/apis/chatting-room`, data, {withCredentials: true})
            .then(res => {
                console.log(res.data.result)
            })

    }
    const chattingRoomRedirect = (uid) => {
        navigate('/chatting-room', {state: {uid}})
    }
    const getChattingMessage = (username) => {
        axios.get(`http://192.168.3.93:8787/apis/chatting-list/${username}`, {withCredentials: true})
            .then(res => {
                setMessages(prevMessages => [...prevMessages, ...res.data])
            })
    }

    const handleUserClick = (username) => {
        if (!userClient.current[username]) {
            subscribeWebsocket(username)
        }
        setSelectedUser(username)
        setMessages([]);
        getChattingMessage(username)
    }

    const subscribeWebsocket = (username) => {
        const chatWebSocket = new WebSocket("ws://192.168.3.93:8787/chat")
        userClient.current[username] = Stomp.over(chatWebSocket);
        userClient.current[username].connect({}, () => {
            userClient.current[username].subscribe(`/chat/room/${username}`, (payload) => {
                const chat = JSON.parse(payload.body)
                setMessages(prevMessage => [...prevMessage, chat])
            })
        });
        return () => {
            userClient.current[username].disconnect();
            chatWebSocket.close();
        }
    }
    return (
        <div>이동
            <br/>
            <br/>
                <button onClick={redirect}>나가기</button>
                <button onClick={createRoom}>채팅방 생성</button>
            <br/>

            <br/>
            <div> 채팅방
                <table>
                    <thead>
                        <tr>
                            <th>채팅방 UID</th>
                            <th>채팅방 참여자 UID</th>
                            <th>채팅방 생성자</th>
                            <th>채팅방 생성일</th>
                        </tr>
                    </thead>
                    <tbody>
                    {chattingRoomList.map(room => (
                        <tr key={room.uid}>
                            <td onClick={() => {chattingRoomRedirect(room.uid)}}>{room.uid}</td>
                            <td>{room.participants}</td>
                            <td>{room.createdBy}</td>
                            <td>{room.createdDate}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
            <br/>
            <div> 회원리스트
                <table>
                    <thead>
                    <tr>
                        <th>회원 UID</th>
                        <th>회원 이름</th>
                    </tr>
                    </thead>
                    <tbody>
                    {memberList.map(member => (
                        <tr key={member.uid}>
                            <td onClick={() => {handleUserClick(member.username)}}>{member.username}</td>
                            <td>{member.username}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
            {selectedUser &&
                <div>
                    <h3>Chat with {selectedUser}</h3>
                    <ul>
                        {messages.map((message, index) => (
                            <li key={index}>{message.content}</li>
                        ))}
                    </ul>
                    <input value={inputMessage} onChange={handleInputMessage}/>
                    <button onClick={handleSendMessage}>입력</button>
                </div>

            }
            <br/>
            <br/>
            <br/>
        </div>
    );
}

export default Chatting;
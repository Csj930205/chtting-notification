import React, {useEffect, useState} from 'react';
import { useAuth } from "../AuthContext";
import axios from "axios";

function TestChatting(props) {
    const {user} = useAuth();
    const [memberList, setMemberList] = useState([]);
    const [selectMember, setSelectMember] = useState('');
    const [socket, setSocket] = useState(null);
    const [inputMessage, setInputMessage] = useState('');
    const [attach, setAttach] = useState(null);

    useEffect(() => {
        axios.get(`http://192.168.3.93:8787/apis/member`, {withCredentials: true})
            .then(res => {
                setMemberList(res.data)
            })
        if (user) {
            const chatWebsocket = new WebSocket(`ws://192.168.3.93:8787/chat?userId=${user.username}`)
            // 연결이 열릴 때
            chatWebsocket.onopen = () => {
                console.log("WebSocket 연결됨");
                setSocket(chatWebsocket); // 소켓 상태 업데이트
            };

            // 연결이 닫힐 때
            chatWebsocket.onclose = () => {
                console.log("WebSocket 연결 종료됨");
            };
            return () => {
                if (socket) {
                    socket.close();
                }
            };
        }
    }, [user]);

    const handleSelectMember = (username) => {
        setSelectMember(username);
    }
    useEffect(() => {
        if (socket) {
            socket.onmessage = (event) => {
                const message = JSON.parse(event.data)
                console.log("수신메세지: ", message)
            }
        }
    }, [socket]);

    const handleInputMessage = (e) => {
        setInputMessage(e.target.value)
    }

    const handleSendMessage = () => {
        setInputMessage('');
        const data = {
            roomUid: 1,
            content: inputMessage,
            senderUid: user.username
        }
        if (socket && socket.readyState === WebSocket.OPEN) {
            if(selectMember) {
                socket.send(JSON.stringify({
                    ...data,
                    receiverUid: selectMember
                }))
            } else {
                socket.send(JSON.stringify(data))
            }
        }
    }

    const handleFileSelect = (event) => {
        const file = event.target.files[0];
        if (!file) {
            return;
        }
        setAttach(file);
    }

    const sendAttach = () => {
        if (!attach) {
            return;
        }
        const reader = new FileReader();
        reader.onload = (e) => {
            const binaryData = e.target.result;
            const data = {
                roomUid: 1,
                content: '',
                senderUid: user.username,
                attach: binaryData
            }
            if (socket && socket.readyState === WebSocket.OPEN) {
                if (selectMember) {
                    socket.send(JSON.stringify({
                        ...data,
                        receiverUid: selectMember
                    }))
                } else {
                    socket.send(JSON.stringify(data))
                }
            }
        }
        reader.readAsArrayBuffer(attach);
    }
    return (
        <div>
            <table>
                <thead>
                <tr>
                    <th>선택</th>
                    <th>회원 UID</th>
                    <th>회원 이름</th>
                </tr>
                </thead>
                <tbody>
                {memberList.filter(members => members.username !== user.username).map(member => (
                    <tr key={member.uid}>
                        <td>
                            <input type="checkbox" onChange={() => handleSelectMember(member.username)}/>
                        </td>
                        <td>{member.username}</td>
                    </tr>
                ))}
                </tbody>
            </table>
            <input value={inputMessage} onChange={handleInputMessage}/>
            <button type='button' onClick={handleSendMessage}>보내기</button>
            <input type='file' onChange={handleFileSelect}/>
            <button type='button' onClick={sendAttach}>전송</button>
        </div>
    );
}

export default TestChatting;
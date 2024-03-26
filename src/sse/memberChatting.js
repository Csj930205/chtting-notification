import React, {useEffect, useRef, useState} from 'react';
import {useNavigate} from "react-router-dom";
import {useAuth} from "../AuthContext";
import axios from "axios";

function MemberChatting(props) {
    const navigate = useNavigate();
    const [memberList, setMemberList] = useState([]);
    const [messages, setMessages] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const {user} = useAuth();
    const {client} = useAuth();
    const [inputMessage, setInputMessage] = useState('');
    const [showDropDown, setShowDropDown] = useState([]);
    const [chattingRoomList, setChattingRoomList] = useState([]);
    const [roomUid, setRoomUid] = useState('');
    const [currentRoomUid, setCurrentRoomUid] = useState('');
    const {socketMessage} = useAuth();
    const {notificationMessage} = useAuth();
    const [selectMember, setSelectMember] = useState([]);
    const [chattingStart, setChattingStart] = useState(false);
    const [attach, setAttach] = useState(null)

    const handleSelectMember = (uid) => {
        if (selectMember.includes(uid)) {
            setSelectMember(selectMember.filter(member => member !== uid))
        } else {
            setSelectMember([...selectMember, uid])
        }
    }

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

        const data = {
            roomUid: roomUid,
            content: inputMessage,
            senderUid: user.username
        }

        console.log("/////////////" + selectedUser)
        client.current.send(`/pub/chat/send-message`, {}, JSON.stringify(data));
    }
    console.log(notificationMessage)
    useEffect(() => {
        axios.get(`http://192.168.3.93:8787/apis/member`, {withCredentials: true})
            .then(res => {
                setMemberList(res.data)
            })
            .catch(error => {
                console.error(error)
            })
        axios.get(`http://192.168.3.93:8787/apis/chatting-room/my-list`, {withCredentials: true})
            .then(res => {
                setChattingRoomList(res.data.chattingRoomList);
            })
    }, []);

    const redirect = () => {
        navigate('/see')
    }

    const handleFileSelect = (event) => {
        const file = event.target.files[0];
        if (!file) {
            return;
        }
        setAttach(file);
    }
    const sendFile = (currentRoomUid) => {
        if (!attach) {
            return;
        }
        const formData = new FormData();
        const data = {
            uid: currentRoomUid,
            tableType : 'chatting'
        }
        const blob = new Blob([JSON.stringify(data)], {type: 'application/json'});
        formData.append('chattingRoom', blob);
        formData.append('uploadFile', attach)
        axios.post(`http://192.168.3.93:8787/apis/attaches/upload`, formData, {withCredentials: true})
            .then((res) => {
                if(res.data.result === 'success') {
                    console.log(res.data.url)
                }
            })
    }

    const getChattingMessage = (uid) => {
        axios.get(`http://192.168.3.93:8787/apis/chatting-list/${uid}`, {withCredentials: true})
            .then(res => {
                setMessages(prevMessages => [...prevMessages, ...res.data])
            })
            .catch(error => {
                console.log(error)
            })
    }

    const handleUserClick = (createdBy) => {
        const data = {
            participants: selectMember,
            createdBy: createdBy
        }
        axios.post(`http://192.168.3.93:8787/apis/chatting-room`, data, {withCredentials: true})
            .then(res => {
                if (res.data.result === 'success') {
                    setSelectMember([]);
                    alert('채팅방 개설')
                } else {
                    alert('이미 존재합니다.')
                }
            })
    }

    const handleGetMessage = (createdBy, uid, participants) => {
        if (!chattingStart) {
            setMessages([]);
            setSelectedUser(participants)
            setRoomUid(uid);
            setCurrentRoomUid(uid);
            getChattingMessage(uid);
            setChattingStart(true)
            alert('채팅시작')
        } else {
            setChattingStart(false)
            leaveChattingRoom(uid, user.username)
            alert('채팅종료')
        }
    }

    const leaveChattingRoom = (uid, username) => {
        const data = {
            chattingRoomUid : uid,
            participantsUid : user.username
        }
        axios.put(`http://192.168.3.93:8787/apis/chatting-room/leave`, data, {withCredentials: true})
    }
    const handleDropdownToggle = () => {
        setShowDropDown(prevState => !prevState);
    }

    useEffect(() => {
        isMyMessage();
    }, [notificationMessage]);

    const isMyMessage = (username) => {
        if (!notificationMessage || !notificationMessage.length) {
            return false;
        }
        const lastMessage = notificationMessage[notificationMessage.length - 1];
        if (!lastMessage || !lastMessage.userList) {
            return false;
        }

        const isUserInclueded = lastMessage.userList.includes(username);
        return isUserInclueded
    }


    return (
        <div>
            <br/>
            <br/>
            <button onClick={redirect}>나가기</button>
            <br/>
            <br/>
            <br/>
            <div>
                <strong>대화방</strong>
                <button onClick={handleDropdownToggle}>+</button>
                <br/>
                <br/>
                {
                    showDropDown && (
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
                                    <li style={{backgroundColor: isMyMessage(member.username) ? 'green' : 'red'}}></li>
                                    <td>{member.username}</td>
                                </tr>
                            ))}

                            </tbody>
                            <button type="button" onClick={() => {handleUserClick(user.username)}}>개설</button>
                        </table>
                    )
                }
            </div>
            <br/>
            <div>
                <strong>대화중인 회원</strong>
                {chattingRoomList.map((chatting, index) => (
                    <li key={index} onClick={() => handleGetMessage(chatting.createdBy, chatting.uid, chatting.participants)}>
                        {chatting.participants
                            .filter(participant => participant.participantsUid !== user.username)
                            .map(participant => participant.participantsUid)
                            .join(', ')
                        }
                        <span>(</span>
                        <span>
                            {chatting.participants
                                .filter(participant => participant.participantsUid === user.username)
                                .map(partipant => partipant.unreadMessage)
                            }
                        </span>
                        <span>)</span>
                    </li>
                ))}
            </div>
            <br/>
            <br/>
            <br/>
            <br/>
            {chattingStart && selectedUser &&
                <div>
                    <h3>채팅</h3>
                    <ul>
                        {messages.filter(messages => messages.roomUid === currentRoomUid.toString()).map((message, index) => (
                            <li key={index}>
                                roomUid : {message.roomUid} <br/>
                                senderUid : {message.senderUid} <br/>
                                content : {message.content}
                            </li>
                        ))}
                        {socketMessage.filter(messages => messages.roomUid === currentRoomUid.toString()).map((message, index) => (
                            <li key={index}>
                                roomUid : {message.roomUid} <br/>
                                senderUid : {message.senderUid} <br/>
                                content : {message.content}
                            </li>
                        ))}
                    </ul>
                    <input value={inputMessage} onChange={handleInputMessage}/>
                    <button onClick={handleSendMessage}>입력</button>
                    <input type="file" onChange={handleFileSelect}/>
                    <button onClick={() => sendFile(currentRoomUid)}>전송</button>
                </div>
            }
            <br/>
            <br/>
            <br/>
            <br/>
            <br/>
        </div>
    );
}

export default MemberChatting;
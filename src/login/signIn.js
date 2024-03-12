import React, {useState} from 'react';
import {useNavigate} from "react-router-dom";
import axios from "axios";

function SignIn(props) {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSignIn = (e) => {
        if (username === '') {
            alert('이름을 입력해주새요')
            return;
        }
        if (password === '') {
            alert('이름을 입력해주새요')
            return;
        }
        e.preventDefault();
        const data = {
            username : username,
            password : password
        }
        axios.post(`http://192.168.3.93:8787/apis/member/signin`, data, {withCredentials: true})
            .then(response => {
                if (response.data === 'success') {
                    alert('회원가입에 성공하였습니다.')
                    navigate('/')
                } else {
                    alert('중복된 이름이 존재합니다.')
                    return;
                }
            })
            .catch(error => {
                console.error(error)
            })
    }

    return (
        <div>
            <h2>회원가입</h2>
            <input type='text' value={username} onChange={(e) => setUsername(e.target.value)}/>
            <input type='text' value={password} onChange={(e) => setPassword(e.target.value)}/>
            <button onClick={handleSignIn}>회원가입</button>
        </div>
    );
}

export default SignIn;
import React, {useState} from 'react';
import {useNavigate} from "react-router-dom";
import axios from "axios";
import {useAuth} from "../AuthContext";

function Login(props) {
    const navigate = useNavigate();
    const {login} = useAuth();
    const [username, setUserName] = useState('최성준');
    const [password, setPassword] = useState('1234');

    const handleLogin = () => {
        const data = {
            username : username,
            password : password
        }
        axios.post(`http://192.168.3.93:8787/apis/member/login`, data, {withCredentials: true})
            .then(response => {
                if(response.data != null) {
                    alert('로그인이 성공하였습니다.')
                    console.log(response.data)
                    login(response.data)
                    navigate(`/see`)
                } else {
                    alert('이름 또는 패스워드가 일치하지 않습니다.')
                }
            })
    }
    const redirectSignInPage = () => {
        navigate('/sign')
    }
    return (
        <div>
            <h2>login</h2>
            <input type='text' value={username} onChange={(e) => setUserName(e.target.value)}/>
            <input type='password' value={password} onChange={(e) => setPassword(e.target.value)}/>
            <button onClick={handleLogin}>로그인</button>
            <button onClick={redirectSignInPage}>회원가입</button>

        </div>
    );
}

export default Login;
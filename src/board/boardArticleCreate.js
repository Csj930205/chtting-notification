import React, {useState} from 'react';
import {useAuth} from "../AuthContext";
import {useLocation, useNavigate} from "react-router-dom";
import axios from "axios";

function BoardArticleCreate(props) {
    const {user} = useAuth();
    const location = useLocation();
    const navigate = useNavigate();
    const boardGroupUid = location.state;
    const [content, setContent] = useState('');

    const handleContent = (e) => {
        setContent(e.target.value)
    }
    console.log(boardGroupUid)
    console.log(user)


    const createBoardArticle = () => {
        if (content.trim() === '') {
            alert('내용을 입력해 주세요.')
            return;
        }
        if (!user.username) {
            alert('로그인을 진행해주세요')
            navigate('/')
            return;
        }
        const data = {
            boardGroupUid : boardGroupUid.boardGroupUid,
            createdBy : user.username,
            content : content
        }
        axios.post(`http://192.168.3.93:8787/apis/board-article`, data, {withCredentials: true})
            .then(response => {
                if (response.data === 'success') {
                    alert('등록 성공')
                    navigate('/board-article', {state: boardGroupUid})
                } else {
                    alert('등록중에 오류가 발생하였습니다.')
                }
            })
    }

    return (
        <div>
            <input type='text' value={content} onChange={handleContent}/>
            <button type='button' onClick={createBoardArticle}>등록</button>
        </div>
    );
}

export default BoardArticleCreate;
import React, {useEffect, useState} from 'react';
import {useAuth} from "../AuthContext";
import {useLocation, useNavigate} from "react-router-dom";
import axios from "axios";

function BoardArticle(props) {
    const navigate = useNavigate();

    const location = useLocation();
    const {boardGroupUid} = location.state;
    const [boardArticleList, setBoardArticleList] = useState([]);

    useEffect(() => {
        axios.get(`http://192.168.3.93:8787/apis/board-article/${boardGroupUid}`, {withCredentials: true})
            .then(response => {
                setBoardArticleList(response.data)
            })
    }, []);

    const redirectCreateBoardArticle = (uid) => {
        navigate('/board-article-create', {state:{boardGroupUid}})
    }
    const redirectBoardGroupList = () => {
        navigate('/board-group')
    }

    return (
        <div>
            <button type='button' onClick={redirectCreateBoardArticle}>글등록</button>
            <button type='button' onClick={redirectBoardGroupList}>나가기</button>
            <table>
                <thead>
                    <tr>
                        <th>uid</th>
                        <th>content</th>
                        <th>createdBy</th>
                        <th>createdDate</th>
                    </tr>
                </thead>
                <tbody>
                {boardArticleList.map((article, index) => (
                    <tr key={index}>
                        <th>{article.uid}</th>
                        <th>{article.content}</th>
                        <th>{article.createdBy}</th>
                        <th>{article.createdDate}</th>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default BoardArticle;
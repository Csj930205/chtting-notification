import React, {useEffect, useState} from 'react';
import axios from "axios";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../AuthContext";

function BoardGroup(props) {
    const navigate = useNavigate();
    const {user} = useAuth();
    const [boardGroupList, setBoardGroupList] = useState([]);
    const [boardGroupUid, setBoardGroupUid] = useState('');
    const [boardGroupUsername, setBoardGroupUsername] = useState('');

    console.log(user)
    useEffect(() => {
        axios.get(`http://192.168.3.93:8787/apis/board-group/list`, {withCredentials: true})
            .then(response => {
                setBoardGroupList(response.data)
            })
    }, []);

    const redirectCreateBoardGroup = () => {
        navigate('/board-group/create')
    }

    const redirectMain = () => {
        navigate('/see')
    }

    const attendBoardGroup = (boardGroupUid) => {
        setBoardGroupUid(boardGroupUid);
        if (boardGroupUid === '') {
            alert('그룹번호가 존재하지않음.')
            return;
        }
        if (!user || !user.username) {
            alert('로그인이 되어있지않습니다. 로그인을 진행해주세요')
            navigate('/')
            return;
        }

        const data = {
            boardGroupUid : boardGroupUid,
            boardGroupUsername : user.username
        }
        axios.post(`http://192.168.3.93:8787/apis/board-group-user`, data, {withCredentials: true})
            .then(response => {
                if (response.data === 'success') {
                    alert('그룹에 참석되었습니다.')
                } else {
                    alert('이미 참석되어있습니다.');
                }
            })
    }

    const redirectBoardArticle = (boardGroupUid) => {
        navigate('/board-article', {state: {boardGroupUid}})
    }

    return (
        <div>
            <table>
                <thead>
                    <tr>
                        <th>uid</th>
                        <th>name</th>
                        <th>mandatory</th>
                        <th>참석</th>
                        <th>그룹이동</th>
                    </tr>
                </thead>
                <tbody>
                {boardGroupList.map((group, index) => (
                    <tr key={index}>
                        <th>{group.uid}</th>
                        <th>{group.name}</th>
                        <th>{group.mandatory}</th>
                        <th>
                            <button type="button" onClick={() => attendBoardGroup(group.uid)}>참석</button>
                        </th>
                        <th>
                            <button type="button" onClick={() => redirectBoardArticle(group.uid)}>그룹이동</button>
                        </th>
                    </tr>
                ))}
                </tbody>
            </table>
            <button type='button' onClick={redirectCreateBoardGroup}>생성</button>
            <button type='button' onClick={redirectMain}>나가기</button>
        </div>
    );
}

export default BoardGroup;
import React, {useState} from 'react';
import axios from "axios";
import {useNavigate} from "react-router-dom";

function BoardGroupCreate(props) {
    const navigate = useNavigate();
    const [name, setName] = useState('');
    const [mandatory, setMandatory] = useState('');

    const handleMandatory = (e) => {
        setMandatory(e.target.value);
    }

    const createBoardGroup = () => {
        if (name.trim() === '') {
            alert('이름은 필수값입니다.')
            return;
        }
        if (mandatory.trim() === '') {
            alert('참석여부를 선택해주세요.')
            return;
        }
        const data = {
            name : name,
            mandatory : mandatory
        }
        axios.post(`http://192.168.3.93:8787/apis/board-group`, data, {withCredentials: true})
            .then(response => {
                if (response.data === 'success') {
                    alert('생성되었습니다.')
                    navigate('/board-group')
                } else {
                    alert('중복된 이름이 존재합니다.')
                }
            })
    }

    return (
        <div>
            <input type="text" value={name} onChange={(e) => setName(e.target.value)}/>
            <select value={mandatory} onChange={handleMandatory}>
                <option value=''> 필수참석여부</option>
                <option value='N'> N</option>
                <option value='Y'> Y</option>
            </select>
            <button type="button" onClick={createBoardGroup}>생성</button>
        </div>
    );
}

export default BoardGroupCreate;
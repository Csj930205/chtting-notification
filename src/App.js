import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import Sse from "./sse/sse";
import Chatting from "./sse/chatting";
import ChattingRoom from "./sse/chattingRoom";
import Login from "./login/login";
import SignIn from "./login/signIn";
import BoardGroup from "./board/boardGroup";
import BoardGroupCreate from "./board/boardGroupCreate";
import BoardArticleCreate from "./board/boardArticleCreate";
import BoardArticle from "./board/boardArticle";
import MemberChatting from "./sse/memberChatting";
import Header from "./header";
import TestChatting from "./sse/testChatting";
import {AuthProvider} from "./AuthContext";

function App() {
  return (
      <AuthProvider>
        <BrowserRouter>
          <Header/>
          <Routes>
            <Route path={'/'} element={<Login/>}/>
            <Route path={'/sign'} element={<SignIn/>}/>
            <Route path={'/board-group'} element={<BoardGroup/>}/>
            <Route path={'/board-article'} element={<BoardArticle/>}/>
            <Route path={'/board-article-create'} element={<BoardArticleCreate/>}/>
            <Route path={'/board-group/create'} element={<BoardGroupCreate/>}/>
            <Route path={'/see'} element={<Sse/>}/>
            <Route path={'/testChatting'} element={<TestChatting/>}/>
            <Route path={'/chatting'} element={<Chatting/>}/>
            <Route path={'/member-chatting'} element={<MemberChatting/>}/>
            <Route path={'/chatting-room'} element={<ChattingRoom/>}/>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
  );
}

export default App;

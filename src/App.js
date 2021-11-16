
import './App.css';
import Navbar from './components/Navbar';
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom'

function App() {
  return (
    <>
    <Router>
    <Navbar />
    <Routes>
      <Route exact path='/'></Route>
    </Routes>
    </Router>
     


    </>
  );
}

export default App;

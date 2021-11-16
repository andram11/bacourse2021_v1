import React, {useState} from 'react'
import logo from '../images/ba-course-logo.PNG'
import '../components/Navbar.css'
import {Link} from 'react-router-dom'
import { HomeOutlined, UnorderedListOutlined , UserOutlined  } from '@ant-design/icons';

function Navbar() {
    const [click]= useState(false)
   
        return (
        <>
        <nav className="navbar">
            <div className="navbar-container">
        <Link to="/" className="navbar-logo"  >
            <header><img src={logo} alt='logo'></img> </header></Link> 
                </div>
     <ul className={click ? 'nav-menu active': 'nav-menu'}>
         <li className='nav-item'>
             <Link to='/' className='nav-links'>
                 <HomeOutlined  style={{ fontSize: '40px', color: 'grey' }} />
             </Link>
         </li>
         <li className='nav-item'>
             <Link to='/course' className='nav-links'>
                 <UnorderedListOutlined  style={{ fontSize: '40px', color: 'grey' }} />
             </Link>
         </li>
         <li className='nav-item'>
             <Link to='/user' className='nav-links'>
                 <UserOutlined  style={{ fontSize: '40px', color: 'grey' }} />
             </Link>
         </li>
     </ul>
        </nav>
        </>
    )
}

export default Navbar

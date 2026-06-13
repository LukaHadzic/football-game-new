import { useState } from 'react'
import { login as loginRequest } from '../api/authApi.js'
import { useAuth } from '../auth/AuthContext.jsx'
import { useNavigate } from "react-router-dom";
import "./LoginPage.css"
import centerPitchImage from "../assets/images/CenterPitchLines.png";


function LoginPage() {

    const [nickOrEmail, setNickOrEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const navigate = useNavigate()

    const { login } = useAuth()

    const handleSubmit = async (event) => {
        event.preventDefault()
        setError('')

        try {
            const response = await loginRequest({
                nickOrEmail,
                password
            })

            login(
                response.data.userDto,
                response.data.accessToken

            )

            console.log("Login success")
            console.log(response.data)
            navigate('/home')
        } catch (err) {
            console.error(err)
            setError(err.message)
        }
    }

    return (
        <div className="PageMainPart">

            <div className="FormDiv">

                <div className="HeadingDiv">
                    <h1>Login</h1>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="InputDiv">
                        <input
                            type="text"
                            value={nickOrEmail}
                            onChange={(e) => setNickOrEmail(e.target.value)}
                            placeholder="Nickname or email"
                        />
                    </div>
                    <div className="InputDiv">
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Password"
                        />
                    </div>

                    <button type="submit">Login</button>
                </form>
                <div className="ErrorDiv">
                    {error && (
                        <p>{error}</p>
                    )}
                </div>
            </div>

            <div className="ImageDiv">
                <img src={centerPitchImage}/>
            </div>

        </div>
    )
}

export default LoginPage
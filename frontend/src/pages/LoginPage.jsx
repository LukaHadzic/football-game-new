import { useState } from 'react'
import { login as loginRequest } from '../api/authApi.js'
import { useAuth } from '../auth/AuthContext.jsx'
import { useNavigate, Link } from "react-router-dom";
import styles from "./LoginPage.module.css"
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

    const handleRegisterClick = async (event) => {

        event.preventDefault()
        setError('')

        try {

            navigate('/register')

        } catch (err) {

            console.error(err)
            setError(err.message)

        }
    }

    return (
        <div className={styles.PageMainPart}>

            <div className={styles.FormDiv}>

                <div className={styles.HeadingDiv}>
                    <h1>Login</h1>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className={styles.InputDiv}>
                        <input
                            type="text"
                            value={nickOrEmail}
                            onChange={(e) => setNickOrEmail(e.target.value)}
                            placeholder="Nickname or email"
                        />
                    </div>
                    <div className={styles.InputDiv}>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Password"
                        />
                    </div>

                    <button className={styles.LoginButton} type="submit">Login</button>
                    <div className={styles.RegisterOptionDiv}>
                        <p>Don't have account? <Link to="/register">Register now.</Link></p>
                    </div>
                    <div className={styles.ErrorDiv}>
                        {error && (
                            <p>{error}</p>
                        )}
                    </div>
                </form>
            </div>

            <div className={styles.ImageDiv}>
                <img src={centerPitchImage}/>
            </div>

        </div>
    )
}

export default LoginPage
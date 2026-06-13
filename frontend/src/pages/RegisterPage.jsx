import {useState} from "react";
import {register} from "../api/authApi.js"
import {useNavigate} from "react-router-dom";
import "./RegisterPage.css"
import centerPitchImage from "../assets/images/CenterPitchLines.png";

function RegisterPage() {

    const [name, setName] = useState('')
    const [surname, setSurname] = useState('')
    const [nick, setNick] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')

    const [message, setMessage] = useState('')
    const [error, setError] = useState('')

    const navigate = useNavigate()

    const handleSubmit = async (event) => {
        event.preventDefault()

        setMessage('')
        setError('')

        try {
            const response = await register({
                name,
                surname,
                nick,
                email,
                password
                })

            setName('')
            setSurname('')
            setNick('')
            setEmail('')
            setPassword('')

            navigate("/register-success", {
                state: {
                    message: response.data
                }
            })
        } catch (err) {
            console.error(err)
            if (err.response?.data) {
                setError(err.response.data)
            } else {
                setError("Registration failed.")
            }
        }
    }


    return(

        <div className="PageMainPart">

            <div className="FormDiv">

                <div className="HeadingDiv">
                    <h1>Register</h1>
                </div>

                <form onSubmit={handleSubmit}>
                    <div className="InputDiv">
                        <input
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="Name"
                        />
                    </div>
                    <div className="InputDiv">
                        <input
                            type="text"
                            value={surname}
                            onChange={(e) => setSurname(e.target.value)}
                            placeholder="Surname"
                        />
                    </div>
                    <div className="InputDiv">
                        <input
                            type="text"
                            value={nick}
                            onChange={(e) => setNick(e.target.value)}
                            placeholder="Nickname"
                        />
                    </div>
                    <div className="InputDiv">
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Email"
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

                    <button type="submit">Register</button>
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

        {/*</div>*/}

        {/*<div>*/}
        {/*    <h1>Register</h1>*/}

        {/*    <form onSubmit={handleSubmit}>*/}

        {/*        <div>*/}
        {/*            <label>Name</label>*/}
        {/*            <br />*/}
        {/*            <input*/}
        {/*                type="text"*/}
        {/*                value={name}*/}
        {/*                onChange={(e) => setName(e.target.value)}*/}
        {/*            />*/}
        {/*        </div>*/}

        {/*        <br />*/}

        {/*        <div>*/}
        {/*            <label>Surname</label>*/}
        {/*            <br />*/}
        {/*            <input*/}
        {/*                type="text"*/}
        {/*                value={surname}*/}
        {/*                onChange={(e) => setSurname(e.target.value)}*/}
        {/*            />*/}
        {/*        </div>*/}

        {/*        <br />*/}

        {/*        <div>*/}
        {/*            <label>Nick</label>*/}
        {/*            <br />*/}
        {/*            <input*/}
        {/*                type="text"*/}
        {/*                value={nick}*/}
        {/*                onChange={(e) => setNick(e.target.value)}*/}
        {/*            />*/}
        {/*        </div>*/}

        {/*        <br />*/}

        {/*        <div>*/}
        {/*            <label>Email</label>*/}
        {/*            <br />*/}
        {/*            <input*/}
        {/*                type="email"*/}
        {/*                value={email}*/}
        {/*                onChange={(e) => setEmail(e.target.value)}*/}
        {/*            />*/}
        {/*        </div>*/}

        {/*        <br />*/}

        {/*        <div>*/}
        {/*            <label>Password</label>*/}
        {/*            <br />*/}
        {/*            <input*/}
        {/*                type="password"*/}
        {/*                value={password}*/}
        {/*                onChange={(e) => setPassword(e.target.value)}*/}
        {/*            />*/}
        {/*        </div>*/}

        {/*        <br />*/}

        {/*        <button type="submit">*/}
        {/*            Register*/}
        {/*        </button>*/}

        {/*    </form>*/}

            {/*{message && (*/}
            {/*    <p>{message}</p>*/}
            {/*)}*/}
        </div>
    )
}

export default RegisterPage
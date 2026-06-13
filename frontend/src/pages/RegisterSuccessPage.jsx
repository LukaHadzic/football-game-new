import {useLocation, useNavigate} from "react-router-dom"

function RegisterSuccessPage() {

    const location = useLocation()
    const navigate = useNavigate()

    const message = location.state?.message

    return (
        <div>
            <h1>Registration successful</h1>

            <p>{message}</p>

            <button onClick={() => navigate('/login')}>
                Go to Login
            </button>
        </div>
    )
}

export default RegisterSuccessPage
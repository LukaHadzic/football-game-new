import {useLocation, useNavigate} from "react-router-dom"
import styles from "./RegisterSuccessPage.module.css"

function RegisterSuccessPage() {

    const location = useLocation()
    const navigate = useNavigate()

    const message = location.state?.message

    return (
        <div>
            <div className={styles.HeadingDiv}>
                <h1>Registration successful!</h1>
            </div>

            <div className={styles.MessageDiv}>
                <p>{message}</p>
            </div>

            <button className={styles.RegisterSuccessButton} onClick={() => navigate('/login')}>
                Go to Login
            </button>
        </div>
    )
}

export default RegisterSuccessPage
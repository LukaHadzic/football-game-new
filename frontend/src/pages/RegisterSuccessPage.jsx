import {useLocation, useNavigate} from "react-router-dom"
import styles from "./RegisterSuccessPage.module.css"

function RegisterSuccessPage() {

    const location = useLocation()
    const navigate = useNavigate()

    const message = location.state?.message

    return (
        <div>
            <div className={styles.HeadingDiv} data-testid={"heading-div"}>
                <h1>Registration successful!</h1>
            </div>

            <div className={styles.MessageDiv} data-testid={"message-div"}>
                <p>{message}</p>
            </div>

            <button className={styles.RegisterSuccessButton} onClick={() => navigate('/login')} data-testid={"login-redirect-button"}>
                Go to Login
            </button>
        </div>
    )
}

export default RegisterSuccessPage
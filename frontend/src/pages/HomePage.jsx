import {useNavigate} from "react-router-dom";
import { useAuth } from "../auth/AuthContext.jsx";
import { logout as logoutApi } from "../api/authApi.js";
import styles from "./HomePage.module.css"

function HomePage() {
    const { user, logout } = useAuth()
    const navigate = useNavigate();

    const handleLogout = async (event) => {

        event.preventDefault()

        try {
            await logoutApi()

            logout()

            navigate('/login')
        } catch (err) {
            console.error(err)
        }
    }


    return(
        <div>
            <div className={styles.DataDiv}>
                <h1>Home</h1>

                <p>Id: {user?.id}</p>
                <p>Nick: {user?.nick}</p>
                <p>Ime: {user?.name}</p>
                <p>Prezime: {user?.surname}</p>
                <p>Email: {user?.email}</p>
                <p>Role: {user?.roles?.join(', ')}</p>
            </div>
            <button className={styles.HomePageButton} onClick={handleLogout} data-testid={"logout-button"}>Logout</button>
        </div>
    )
}

export default HomePage
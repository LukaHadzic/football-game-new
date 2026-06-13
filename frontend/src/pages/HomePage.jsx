import { useAuth } from "../auth/AuthContext.jsx";

function HomePage() {
    const { user } = useAuth()

    return(
        <div>
            <h1>Home</h1>

            <p>Id: {user?.id}</p>
            <p>Nick: {user?.nick}</p>
            <p>Ime: {user?.name}</p>
            <p>Prezime: {user?.surname}</p>
            <p>Email: {user?.email}</p>
            <p>Role: {user?.roles?.join(', ')}</p>
        </div>
    )
}

export default HomePage
import { createContext, useContext, useState, useEffect } from "react";
import {refresh} from "../api/authApi.js";

const AuthContext = createContext()

export function AuthProvider({ children }){
    const [user, setUser] = useState(null)
    const [accessToken, setAccessToken] = useState(null)
    const [loading, setLoading] = useState(true)

    const login = (userDto, token) => {
        setUser(userDto)
        setAccessToken(token)
    }

    const logout = () => {
        setUser(null)
        setAccessToken(null)
    }

    useEffect(() => {

        const initializeAuth = async() => {

            try {
                const response = await refresh()

                setUser(response.data.accessToken)

            } catch (err) {

                console.log(err.message)
                console.error(err)

            } finally {
                setLoading(false)
            }
        }

        initializeAuth()

    }, []);

    if (loading) {
        return <div>Loading...</div>
    }
    return(
        <AuthContext.Provider
            value={{
                user,
                accessToken,
                loading,
                login,
                logout
            }}>
            { children }
        </AuthContext.Provider>
    )
}

export function useAuth() {
    return useContext(AuthContext)
}
import axios from 'axios';

const authApi = axios.create({
    // baseURL: 'http://localhost:8080/auth',
    baseURL: import.meta.env.VITE_AUTH_API_URL,
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
})

export default authApi

export const login = (data) => {
    return authApi.post('/login', data)
}

export const register = (data) => {
    return authApi.post('/register', data)
}

export const refresh = () => {
    return authApi.post('/refresh')
}

export const logout = () => {
    return authApi.post('/logout')
}
import axios from 'axios';

const authApi = axios.create({
    baseURL: 'http://localhost:8080/auth',
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
import axios from 'axios'

export const notify = (message, type = 'success') => {
    window.dispatchEvent(new CustomEvent('app:notice', { detail: { message, type } }))
}

const request = axios.create({
    baseURL: '/api',
    timeout: 60000,       
})

request.interceptors.request.use(
    (config) => {
        const loginUser = localStorage.getItem('loginUser')
        if (loginUser) {
            try {
                const user = JSON.parse(loginUser)
                if (user?.token) {
                    config.headers.token = user.token
                }
            } catch (e) {
                localStorage.removeItem('loginUser')
            }
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    (response) => {
        return response.data  
    },

    (error) => {
        let errorMessage = 'API request failed'  

        if (error.response) {
            const data = error.response.data

            if (data?.message) {
                errorMessage = data.message
            } else if (data?.msg) {
                errorMessage = data.msg
            } else if (data?.errorMsg) {
                errorMessage = data.errorMsg
            } else if (typeof data === 'string') {
                errorMessage = data
            } else if (data?.error) {
                errorMessage = data.error
            }

            if (error.response.status === 401 && error.config?.url !== '/login') {
                localStorage.removeItem('loginUser')
                notify('Session expired. Please sign in again.', 'error')
                window.location.assign('/login')
                return Promise.reject(error)
            }

        } else if (error.request) {
            errorMessage = 'Network error. Check your connection and try again'
        } else {
            errorMessage = error.message || 'Request configuration error'
        }

        notify(errorMessage, 'error')

        return Promise.reject(error)  
    }
)

export default request

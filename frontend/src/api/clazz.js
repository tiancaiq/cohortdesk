import request from '../utils/request'

export const getClazzList = (params) => {
    return request.get('/clazzs', { params })
}

export const deleteClazz = (id) => {
    return request.delete(`/clazzs/${id}`)
}

export const addClazz = (data) => {
    return request.post('/clazzs', data)
}

export const getClazzById = (id) => {
    return request.get(`/clazzs/${id}`)
}

export const updateClazz = (data) => {
    return request.put('/clazzs', data)
}

export const getAllClazz = () => {
    return request.get('/clazzs/list')
}
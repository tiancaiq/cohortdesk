import request from '../utils/request'


export const getStudentList = (params) =>
    request.get('/students', { params })

export const getStudentById = (id) =>
    request.get(`/students/${id}`)

export const addStudent = (data) =>
    request.post('/students', data)

export const updateStudent = (data) =>
    request.put('/students', data)

export const deleteStudents = (ids) =>
    request.delete(`/students/${ids.join(',')}`)

export const handleViolation = (id, score) =>
    request.put(`/students/violation/${id}/${score}`)
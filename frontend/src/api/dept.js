import request from "../utils/request"

export const queryAllApi = () => request.get('/depts')

export const addDeptApi = (data) => request.post('/depts', data)

export const queryInfoApi = (id) => request.get(`/depts/${id}`)

export const updateDeptApi = (data) => request.put('/depts', data)

export const deleteDeptApi = (id) => request.delete(`/depts?id=${id}`)
import request from "../utils/request";

export const queryPageApi = (name,gender,begin,end,page,pageSize) =>
    request.get('/emps', { params: { name: name || undefined, gender: gender || undefined, begin: begin || undefined, end: end || undefined, page, pageSize } })

export const queryAllApi = () =>  request.get(`/emps/list`);

export const addApi = (emp) =>  request.post('/emps', emp);

export const queryInfoApi = (id) =>  request.get(`/emps/${id}`);

export const updateApi = (emp) =>  request.put('/emps', emp);

export const deleteApi = (ids) =>  request.delete(`/emps?ids=${ids}`);

export const updatePasswordApi = ( data ) =>  request.put( '/emps/password', data );

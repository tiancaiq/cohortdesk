import request from '../../utils/request'


export const getPageApi = (params) => {
    return request({
        url: '/logs/operation',
        method: 'GET',
        params
    })
}

export const getSummaryDataApi = () => {
    return request({
        url: '/logs/operation/summary',
        method: 'GET'
    })
}
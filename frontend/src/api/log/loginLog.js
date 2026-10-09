import request from '../../utils/request'

export function pageLoginLog(params) {
    return request({
        url: '/logs/login',
        method: 'get',
        params
    })
}

export function getLoginLogSummaryData() {
    return request({
        url: '/logs/login/summary',
        method: 'get'
    })
}
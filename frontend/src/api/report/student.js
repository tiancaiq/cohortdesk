import request from '../../utils/request'

export const getStudentDegreeData = () => {
    return request.get('/report/studentDegreeData')
}

export const getStudentCountData = () => {
    return request.get('/report/studentCountData')
}

export const getStudentSummaryData = () => {
    return request.get( '/report/studentSummaryData' )
}

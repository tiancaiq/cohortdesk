import request from '../../utils/request'

export const getEmpGenderData = () => request.get('/report/empGenderData')

export const getEmpJobData = () => request.get('/report/empJobData')
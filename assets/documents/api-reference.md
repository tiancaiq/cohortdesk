# API Reference

Base URL: `http://127.0.0.1:8081`  
Frontend development proxy: `http://127.0.0.1:5173/api`

Except for `POST /login`, requests require the JWT returned by login in a header named `token`. JSON requests use `Content-Type: application/json`.

## Response format

Most endpoints return:

```json
{
  "code": 1,
  "msg": "success",
  "data": {}
}
```

`code: 1` indicates success; `code: 0` indicates an application error. Paged results use `data.total` and `data.rows`.

Incorrect credentials and missing, invalid, or expired tokens return HTTP 401 with `code: 0`. Validation errors return HTTP 400.

## Authentication

| Method | Path | Request | Result |
| --- | --- | --- | --- |
| POST | `/login` | JSON `username`, `password` | Employee identity and `token` |
| PUT | `/emps/password` | JSON `oldPassword`, `newPassword` | Success response |

Example login:

```json
{"username":"linchong","password":"123456"}
```

## Departments

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/depts` | List departments |
| GET | `/depts/{id}` | Get one department |
| POST | `/depts` | Add a department; JSON `name` |
| PUT | `/depts` | Update a department; JSON `id`, `name` |
| DELETE | `/depts?id={id}` | Delete a department |

## Employees

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/emps` | Paged search; optional `name`, `gender`, `begin`, `end`, `page`, `pageSize` |
| GET | `/emps/list` | List employees for selection controls |
| GET | `/emps/{id}` | Get an employee, including work experience |
| POST | `/emps` | Add an employee |
| PUT | `/emps` | Update an employee |
| DELETE | `/emps?ids=1,2` | Delete employees by ID |

Employee JSON fields include `username`, `name`, `gender`, `phone`, `job`, `salary`, `image`, `entryDate`, `deptId`, and `exprList`. Work experience items use `company`, `job`, `begin`, and `end`. Passwords are never returned; use `PUT /emps/password` to change the signed-in employee's password.

## Cohorts

The existing API path is spelled `clazzs`; clients must use that exact spelling.

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/clazzs` | Paged search; optional `name`, `begin`, `end`, `page`, `pageSize` |
| GET | `/clazzs/list` | List cohorts |
| GET | `/clazzs/listHeadTeachers` | List employees available as cohort leads |
| GET | `/clazzs/{id}` | Get one cohort |
| POST | `/clazzs` | Add a cohort |
| PUT | `/clazzs` | Update a cohort |
| DELETE | `/clazzs/{id}` | Delete a cohort |

Cohort JSON fields include `name`, `room`, `beginDate`, `endDate`, `masterId`, and `subject`. Subject codes are 1 Java, 2 Frontend, 3 Data Engineering, 4 Python, 5 Go, and 6 Embedded Systems.

## Learners

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/students` | Paged search; optional `name`, `degree`, `clazzId`, `page`, `pageSize` |
| GET | `/students/list` | List learners |
| GET | `/students/{id}` | Get one learner |
| POST | `/students` | Add a learner |
| PUT | `/students` | Update a learner |
| DELETE | `/students/{ids}` | Delete comma-separated learner IDs |
| PUT | `/students/violation/{id}/{score}` | Record incident points for a learner |

Learner JSON fields include `name`, `no` (learner ID), `gender`, `phone`, `idCard`, `isCollege`, `address`, `degree`, `graduationDate`, and `clazzId`.

## Reports and logs

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/report/empJobData` | Employee counts by role |
| GET | `/report/empGenderData` | Employee counts by gender |
| GET | `/report/studentCountData` | Learner counts by cohort |
| GET | `/report/studentDegreeData` | Learner education levels |
| GET | `/report/studentSummaryData` | Learner and cohort summary |
| GET | `/logs/operation` | Paged operation logs; `operateEmpId`, `beginDate`, `endDate`, `page`, `pageSize` |
| GET | `/logs/operation/summary` | Operation totals and timing |
| GET | `/logs/login` | Paged login logs; `username`, `beginDate`, `endDate`, `page`, `pageSize` |
| GET | `/logs/login/summary` | Login outcomes and totals |

Login logs contain the username, time, outcome, and duration. Operation logs record the employee, action, time, and outcome; password-change parameters and response payloads are excluded.

## Upload

`POST /upload` accepts multipart form data with a field named `file` and returns an image URL. The current implementation requires configured Aliyun OSS credentials.

## Notes

The endpoint shapes above reflect the current controllers. This is a demo API: authentication, sensitive-field handling, and validation need further work before public deployment.

# CohortDesk React frontend

React 19 and Vite 8 frontend for the training operations dashboard. It uses React Router for navigation, Axios for the existing Spring Boot API, and ECharts for reports.

From this directory:

```sh
npm ci
npm run dev
```

Open `http://127.0.0.1:5173/login`. The development server proxies `/api` to the Spring Boot backend on port 8081. See the repository root README for database and backend setup and the demo login.

To make a production bundle, run `npm run build`; output is written to `dist/`.

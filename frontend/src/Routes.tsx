import { Route, Routes } from "react-router-dom"

import App from "./App"
import AuthorizeCallbackPage from "./pages/auth/AuthorizeCallback"
import AuthenticatedPage from "./pages/auth/Authenticated"


const AppRoutes = () => {

    return (
        <>
            <Routes>
                <Route path="/" element={<App />} />
                <Route path="/auth/callback" element={<AuthorizeCallbackPage />} />
                <Route path="/auth/authticated" element={<AuthenticatedPage />} />
                <Route path="*" element={<App />} />
            </Routes>
        </>
    )
}

export default AppRoutes
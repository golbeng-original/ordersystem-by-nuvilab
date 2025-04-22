import { Route, Routes } from "react-router-dom"

import App from "./App"
import AuthorizeCallback from "./pages/auth/AuthorizeCallback"
import Authenticated from "./pages/auth/Authenticated"


const AppRoutes = () => {

    return (
        <>
            <Routes>
                <Route path="/" element={<App />} />
                <Route path="/auth/callback" element={<AuthorizeCallback />} />
                <Route path="/auth/authticated" element={<Authenticated />} />
                <Route path="*" element={<App />} />
            </Routes>
        </>
    )
}

export default AppRoutes
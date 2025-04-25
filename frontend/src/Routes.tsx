import { Route, Routes } from "react-router-dom"

import AuthorizeCallbackPage from "./pages/auth/AuthorizeCallbackPage"
import AuthenticatedPage from "./pages/auth/AuthenticatedPage"
import HomePage from "./pages/home/HomePage"
import StoreListPage from "./pages/stores/StoreListPage"
import StoreMenuListPage from "./pages/stores/StoreMenuListPage"
import OrderCompletePage from "./pages/stores/OrderCompletePage"
import MyOrdreListPage from "./pages/user/MyOrderListPage"


const AppRoutes = () => {

    return (
        <>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/auth/callback" element={<AuthorizeCallbackPage />} />
                <Route path="/auth/authticated" element={<AuthenticatedPage />} />
                <Route path="/stores" element={<StoreListPage />} />
                <Route path="/stores/:storeId" element={<StoreMenuListPage />} />
                <Route path="/order/:orderId" element={<OrderCompletePage />} />
                <Route path="/user/orders" element={<MyOrdreListPage />} />
                <Route path="*" element={<HomePage />} />
            </Routes>
        </>
    )
}

export default AppRoutes
import { useCallback, useState } from "react";
import { useHttpClient } from "../../../shared/http/httpClientContext"

export interface OrderMenuItem {
    menuItemId: string;
    quantity: number;
}

export interface OrderPayload {
    storeId: number,
    menuItems:OrderMenuItem[],
    receiveAddressId: string,
    paymentMethod: string
}

interface OrderResponse {
    orderId: string;
}

const useRequestOrder = () => {

    const { httpClient } = useHttpClient();

    const [orderId, setOrderId] = useState<string | null>(null);

    const requestOrder = useCallback(async (orderPayload:OrderPayload) => {

        const response = await httpClient.post<OrderResponse>({
            path: '/orders',
            body: orderPayload
        });

        setOrderId(response.data.orderId);

    }, []);


    return { orderId, requestOrder };

}

export default useRequestOrder;
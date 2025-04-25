import { useCallback, useState } from "react";
import { useHttpClient } from "../../../shared/http/httpClientContext";

export interface OrderedMenuItem {
    menuItemId: string;
    menuName: string;
    quantity: number;
    unitPrice: number;
}

export interface OrderPayload {
    orderId: string;
    storeId: number;
    orderUserId: number;
    orderStatus: string;
    orderedAt: string,
    orderedItems: OrderedMenuItem[];
    paymentInfo: {
        paymentMethod: string;
    };
    deliveryInfo: {
        address: string;
        receiverName: string;
        receiverPhoneNumber: string;
    }
}

export interface GetOrderResponse {
    order: OrderPayload;
}

const useGetOrder = () => {
    const { httpClient } = useHttpClient();

    const [orderPayload, setOrderPayload] = useState<OrderPayload | null>(null);

    const requestOrderInfo = useCallback(async (orderId: string) => {

        const response = await httpClient.get<GetOrderResponse>({
            path: `/orders/${orderId}`
        });

        setOrderPayload(response.data.order);
    }, []);

    return {orderPayload, requestOrderInfo};
}

export default useGetOrder;
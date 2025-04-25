import { create } from "axios";
import { persist } from "zustand/middleware";

export interface OrderMenuForm {
    menuItemId: string;
    quantity: number;
}

export interface ReciveAddressForm {
    addressId: string;
    address: string;
    ownerName: string;
    
}

export interface OrderForm {
    storeId: number;
    storeName: string;
    menus: OrderMenuForm[];
    addressId: string;
}

type OrderFormState = {
    orderForm: OrderForm;
}

type OrderFormActions = {
    updateOrderForm: (orderForm: OrderForm) => void;
}
/*
const useStoreOrderForm = create(
    persist<OrderFormState & OrderFormActions>(
        (set) => ({
            stores: [],
            //
            updateStores: (stores: StoreEntity[]) => set({ 
                stores: [...stores]
            }),
        }),
        {
            name: 'stores-storage'
        }
    )
);

export default useStoreStores;
*/
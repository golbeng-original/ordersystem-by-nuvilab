import { create } from "zustand";
import { persist } from "zustand/middleware";

export interface StoreMenuItemEntity {
    id:string;
    name:string;
    desc:string;
    price:number;
}

export interface StoreEntity {
    id:number;
    name:string;
    desc:string;
    menus:StoreMenuItemEntity[];
}

type StoresState = {
    stores: StoreEntity[];
}

type StoresActions= {
    updateStores: (stores: StoreEntity[]) => void;
}


const useStoreStores = create(
    persist<StoresState & StoresActions>(
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

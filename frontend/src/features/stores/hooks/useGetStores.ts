import { useEffect } from "react";
import { useHttpClient } from "../../../shared/http/httpClientContext"
import useStoreStores, { StoreEntity, StoreMenuItemEntity } from "../store/storeStores";


interface MenuItemPayload {
    menuItemId: string;
    name: string;
    desc: string;
    price: number;
}

interface StorePayload {
    storeId: number;
    ownerUserId: number;
    name: string,
    desc: string,
    menus:MenuItemPayload[]
}

interface GetStoresResponsePayload {
    stores: StorePayload[]
}

const useGetStores = () => {

    const { httpClient } = useHttpClient();
    const { stores, updateStores } = useStoreStores()

    useEffect(() => {

        const getStores = async () => {
            const response = await httpClient.get<GetStoresResponsePayload>({
                path: '/stores'
            });

            const storeEntities = response.data.stores.map((store: StorePayload) : StoreEntity => {
                
                return {
                    id: store.storeId,
                    name: store.name,
                    desc: store.desc,
                    menus: store.menus.map((menuItem: MenuItemPayload):StoreMenuItemEntity => {
                        return {
                            id: menuItem.menuItemId,
                            name: menuItem.name,
                            desc: menuItem.desc,
                            price: menuItem.price
                        }
                    })   
                }
            });

            updateStores(storeEntities);
        };

        getStores();

    }, []);

    return { stores }

}

export default useGetStores;
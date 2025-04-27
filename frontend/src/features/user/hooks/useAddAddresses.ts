import { useCallback } from "react";
import { useHttpClient } from "../../../shared/http/httpClientContext";
import useStoreUser from "../store/storeUser";

interface AddAddressResponsePayload {
    address: {
        addressId: string;
        name: string;
        address: string;
        phoneNumber: string;
    }
}

const useAddAddresses = () => {

    const { httpClient } = useHttpClient();

    const { addresses, addNewAddresses } = useStoreUser();
    
    const requestAddress = useCallback(async (params:{
        name: string,
        address: string,
        phoneNumber: string
    }) => {

        const { name, address, phoneNumber } = params;

        const response = await httpClient.post<AddAddressResponsePayload>({
            path: '/user/addresses',
            body: {
                name: name,
                address: address,
                phoneNumber: phoneNumber
            }
        });

        addNewAddresses({
            id: response.data.address.addressId,
            ownerName: response.data.address.name,
            address: response.data.address.address,
            phoneNumber: response.data.address.phoneNumber
        })

    }, []);

    return { addresses, requestAddress }

}

export default useAddAddresses;
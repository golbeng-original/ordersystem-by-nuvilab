import { useEffect } from "react"
import { useHttpClient } from "../../../shared/http/httpClientContext";
import useStoreUser, { UserAddressEntity } from "../store/storeUser";

interface AddressPayload {
    addressId: string;
    name: string;
    address: string;
    phoneNumber: string;
}

interface GetAddressesResponsePayload {
    addresses: AddressPayload[]
}


const useGetAddresses = () => {

    const { httpClient } = useHttpClient();
    const { addresses, updateUserAddresses } = useStoreUser();

    useEffect(() => {

        const requestGetAddress = async () => {
            const response = await httpClient.get<GetAddressesResponsePayload>({
                path: '/user/addresses'
            })

            const addressEntities = response.data.addresses.map((address: AddressPayload):UserAddressEntity => {
                return {
                    id: address.addressId,
                    ownerName: address.name,
                    address: address.address,
                    phoneNumber: address.phoneNumber
                }
            });

            updateUserAddresses(addressEntities);
        };

        requestGetAddress();

    }, [])

    return { addresses }

}

export default useGetAddresses;
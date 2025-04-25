import { create } from "zustand";
import { persist } from "zustand/middleware";

export interface UserEntity {
    id: number;
    email: string;
}

export interface UserAddressEntity {
    id: string;
    address: string;
    ownerName: string;
    phoneNumber: string;
}

type UserState = {
    user: UserEntity | null;
    addresses: UserAddressEntity[];
}

type UserActions = {
    updateUser: (user: UserEntity) => void;
    updateUserAddresses: (userAddresses: UserAddressEntity[]) => void;
}

const useStoreUser = create(
    persist<UserState & UserActions>(
        (set) => ({
            user: null,
            addresses: [],  
            //
            updateUser: (user: UserEntity) => {
                set({
                    user: {
                        ...user
                    }
                })
            },
            updateUserAddresses: (addresses: UserAddressEntity[]) => {
                set({
                    addresses: [...addresses]
                })
            }
        }),
        {
            name: 'user-storage'
        }
    )
);

export default useStoreUser;
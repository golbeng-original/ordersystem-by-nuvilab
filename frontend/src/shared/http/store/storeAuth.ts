import { create } from 'zustand';
import { persist } from 'zustand/middleware';

type AuthState = {
    token: string | null;
}

type AuthActions = {
    updateToken: (token: string) => void;
    clearToken: () => void;
}

const useStoreAuth = create(
    persist<AuthState & AuthActions>(
        (set) => ({
            token: null,
            //
            updateToken: (token: string) => set({ token }),
            clearToken: () => set({ token: null }),
        }),
        {
            name: 'auth-storage'
        }
    )
);

export default useStoreAuth;
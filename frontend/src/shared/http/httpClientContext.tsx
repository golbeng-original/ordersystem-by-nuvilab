import { createContext, PropsWithChildren, useContext, useEffect } from "react";
import HttpClient from "./httpClient";
import useStoreAuth from "./store/storeAuth";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

const httpClient = new HttpClient(BASE_URL);

const HttpClientContext = createContext<HttpClient | undefined>(undefined);

export const HttpClientProvider = (props:PropsWithChildren) => {
    return (
        <HttpClientContext.Provider value={httpClient}>
            {props.children}
        </HttpClientContext.Provider>
    )
}

export const useHttpClient = () => {
    const httpClient = useContext(HttpClientContext);
    if( !httpClient ) {
        throw new Error('useHttpClient must be used within a HttpClientProvider');
    }

    const { token, updateToken, clearToken } = useStoreAuth();

    useEffect(() => {
        httpClient.registerNewTokenResolver((newToken: string) => {
            updateToken(newToken);
        });

        httpClient.registerClearTokenResolver(() => {
            clearToken();
        });

    }, [updateToken, clearToken])

    useEffect(() => {
        if( token ) {
            httpClient.updateAuthorization(token)
        }
    }, [token])

    const updateTokenForHttpClient = (token: string) => {
        httpClient.updateAuthorization(token);
        updateToken(token);
    }



    return { httpClient, updateTokenForHttpClient };
}
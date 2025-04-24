import { useCallback, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import { useHttpClient } from "../../../shared/http/httpClientContext";

type AuthorizeResponsePayload = {
    eamil: string;
    jwtToken: string;
    userId: number;
}

const AuthorizeCallbackWidget = () => {

    const [searchParams] = useSearchParams();
    const { httpClient, updateTokenForHttpClient } = useHttpClient();

    const onAuthorize = useCallback(async (authorizeCode:string) => {

        const body = {
            authorizeCode: authorizeCode,
        }

        const response = await httpClient.post({
            path: '/auth/authorize',
            body: body
        });

        const payload = response.data as AuthorizeResponsePayload;

        updateTokenForHttpClient(payload.jwtToken);

        window.location.href = 'http://localhost:8080/auth/authticated';

    }, []);
    

    useEffect(() => {
        const authorizeCode = searchParams.get('code')
        onAuthorize(authorizeCode as string)
    }, []);

    return (
        <></>
    );
}

export default AuthorizeCallbackWidget;
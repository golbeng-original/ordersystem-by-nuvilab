import { useCallback } from "react"
import { useHttpClient } from "../../../shared/http/httpClientContext"


type AuthLoginResponsePayload = {
    loginUrl:string
}

export const userGoogleAuthLogin = () => {
    
    const { httpClient } = useHttpClient();

    const onLogin = useCallback(async () => {
        const response = await httpClient.get<AuthLoginResponsePayload>({
            path: 'auth/login'
        })

        window.location.href = response!.data.loginUrl;

    }, [httpClient])

    return { onLogin }
}
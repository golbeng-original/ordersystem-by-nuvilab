import { Box, Typography } from "@mui/material";
import { useHttpClient } from "../../shared/http/httpClientContext";
import { useEffect } from "react";
import useStoreAuth from "../../shared/http/store/storeAuth";

const AuthenticatedPage = () => {

    const { httpClient } = useHttpClient();
    const { token } = useStoreAuth();

    useEffect(() => {

        console.log(token)

        const requestTest = async () => {
            await httpClient.get({
                path: '/user'
            });
        }

        requestTest();
    }, [])

    return (
        <Box>
            <Typography>
                Authenticated
            </Typography>
        </Box>
    )    
}

export default AuthenticatedPage;
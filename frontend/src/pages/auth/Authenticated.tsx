import { Box, Typography } from "@mui/material";
import { useHttpClient } from "../../shared/http/httpClientContext";
import { useEffect } from "react";

const Authenticated = () => {

    const { httpClient } = useHttpClient();

    useEffect(() => {

        const requestTest = async () => {

            await httpClient.get({
                path: '/auth/test'
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

export default Authenticated;
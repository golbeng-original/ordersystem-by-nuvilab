import { Box, Typography } from "@mui/material";
import { useEffect } from "react";
import useStoreAuth from "../../shared/http/store/storeAuth";
import { useNavigate } from "react-router-dom";

const AuthenticatedPage = () => {

    const { token } = useStoreAuth();

    const navigator = useNavigate();

    useEffect(() => {
        console.log(token)
        navigator('/stores')
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
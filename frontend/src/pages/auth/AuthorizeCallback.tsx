import { Box } from "@mui/material";
import AuthorizeCallbackWidget from "../../features/auth/widgets/AuthorizeCallbackWidget";

const AuthorizeCallback = () => {

    return (
        <Box sx={{
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            margin: '10px',
        }}>
            <AuthorizeCallbackWidget />
            <Box sx={{
                with: 100,
                height: 100,
                backgroundColor: '#f0f0f0',
            }}>
            </Box>
        </Box>
    );
}

export default AuthorizeCallback;
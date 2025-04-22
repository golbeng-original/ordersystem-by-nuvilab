import { Box, Button, Typography } from "@mui/material"
import { useHttpClient } from "../../../shared/http/httpClientContext";

const LoginWidget = () => {

    const { httpClient } = useHttpClient();

    const onHandleLogin = () => {
        const googleOauthLoginUrl = 'https://accounts.google.com/o/oauth2/v2/auth';

        const params = new URLSearchParams({
            client_id: '863068534586-392duo2jfpdkr8usa37fjmt586g95c9a.apps.googleusercontent.com',
            redirect_uri: 'http://localhost:8080/auth/callback',
            scope: 'email profile openid',
            response_type: 'code',
            access_type: 'offline',
            prompt: 'consent'
        })

        httpClient.redirectOtherHost({
            url: googleOauthLoginUrl,
            urlSearchParams: params
        })
    }

    return (
        <Box sx={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            backgroundColor: '#f0f0f0',
            margin: '10px',
        }}>
            <Button
                sx={{ margin: '10px' }}
                variant="contained" 
                color="primary"
                onClick={onHandleLogin} 
            >
                <Typography>
                    Login
                </Typography>
            </Button>
        </Box>

    )
}

export default LoginWidget
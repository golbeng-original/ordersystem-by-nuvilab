import { Box, Button, Paper, Typography } from '@mui/material'
import GoogleIcon from '@mui/icons-material/Google'
import { userGoogleAuthLogin } from '../../features/auth/hooks/useAuthLogin';
import useStoreAuth from '../../shared/http/store/storeAuth';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const GoogleLoginButton = () => {

	const { onLogin } = userGoogleAuthLogin();

	return (
		<Box sx={{ textAlign: "center", mt: 2 }}>
    		<Button
    			variant="outlined"
        		startIcon={<GoogleIcon />}
				onClick={() => { onLogin() }}
        		sx={{
          			textTransform: "none",
          			borderRadius: 3,
          			borderColor: "#d3d3d3",
          			color: "#555",
          			fontWeight: 500,
          			"&:hover": {
					  borderColor: "#aaa",
          			  backgroundColor: "#f7f7f7",
          			},
          			width: "100%",
          			py: 1.5,
        		}}
      		>
        		Continue with Google
      		</Button>
    	</Box>
  );
}

const HomePage = () => {
	const { token } = useStoreAuth();
	const navigate = useNavigate();

	useEffect(() => {
		if( token ) {
			navigate('/stores');
		}
	}, []);

    return (
        <Box
            sx={{
              background: 'linear-gradient(to right, #667eea, #764ba2)',
              minWidth: '100vh',
              minHeight: '100vh',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
        >
            <Paper elevation={6} sx={{ p: 5, borderRadius: 4, width: '100%', maxWidth: 400 }}>
              <Typography variant='h4' align='center' gutterBottom>
                Login
              </Typography>
              <Typography variant='body2' align='center' color='text.secondary' mb={3}>
                Please login to your account
              </Typography>

              <GoogleLoginButton />
            </Paper>
        </Box>
    )
}

export default HomePage
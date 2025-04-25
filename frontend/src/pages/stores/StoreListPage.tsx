import { Box, Button, Card, CardActionArea, CardContent, Container, Grid, Typography } from "@mui/material";
import StorefrontIcon from "@mui/icons-material/Storefront";
import { useNavigate } from "react-router-dom";
import useGetStores from "../../features/stores/hooks/useGetStores";
import { StoreEntity } from "../../features/stores/store/storeStores";

const StoreItem = (props:{
    store:StoreEntity
}) => {
    const { id, name, desc } = props.store;

    const navigator = useNavigate();

    const onHandleStoreClick = () => {
        navigator(`/stores/${id}`);
    }

    return (
        <Grid>
            <Card sx={{
                width: '180px'
            }}>
                <CardActionArea onClick={() => onHandleStoreClick()}>
                    <Box
                        sx={{
                            display: "flex",
                            justifyContent: "center",
                            alignItems: "center",
                            height: 140,
                            backgroundColor: "#f5f5f5",
                        }}
                    >
                        <StorefrontIcon sx={{ fontSize: 64, color: "primary.main" }} />
                    </Box>
                    <CardContent>
                        <Typography gutterBottom variant="h5" component="div">
                            {name}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                            {desc}
                        </Typography>
                    </CardContent>
                </CardActionArea>
            </Card>
        </Grid>
    );
}


const StoreListPage = () => {

    const navigate = useNavigate();
    const { stores } = useGetStores();

    const onHandleGoMyOrder = () => {
        navigate("/user/orders")
    }

    return (
        <Container sx={{
            background: 'linear-gradient(to right, #667eea, #764ba2)',
            width: '100vw',
            height: '100vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            flexDirection: 'column',
        }}>
            <Typography variant="h4" gutterBottom>
                상점 목록
            </Typography>
            <Grid container spacing={4}>
                {stores.map((store) => (
                  <StoreItem key={store.id} store={store}/>
                ))}
            </Grid>
            <Box>
                <Button
                    variant="contained" 
                    color="primary" 
                    sx={{ mt: 3 }}
                    onClick={onHandleGoMyOrder}
                >
                    내 주문 목록
                </Button>
            </Box>
      </Container>
    );
}

export default StoreListPage;